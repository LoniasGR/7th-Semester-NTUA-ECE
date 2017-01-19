#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <string.h>
#include <assert.h>
#include <stdbool.h>

#include <sys/wait.h>
#include <sys/types.h>

#include "proc-common.h"
#include "request.h"

/* Compile-time parameters. */
#define SCHED_TQ_SEC 2                /* time quantum */
#define TASK_NAME_SZ 60               /* maximum size for a task's name */
#define SHELL_EXECUTABLE_NAME "shell" /* executable for shell */


/************* DEFINITIONS ***********************************/

struct processList;
struct process;
static void child(struct process*);

/*************************************************************/


/************* GLOBAL VARIABLES ******************************/

static struct processList* procList;
static struct process* current;

/* used to see if SIGCHLD was called thanks to a shell action
 * 0 is for SIGKILL
 * 1 is for SIGSTOP from starting a new process */

static bool exceptions [2] = {false, false};
static pid_t  exceptionsID [2];

/************************************************************/

/* a process has a pNumber given by us, a PID given by the kernel and
 * the name of the executable that is running.*/
struct process{
		int pNumber;
		pid_t pPid;
		char* execName;
		struct process* next;
};

/* allocates space in the heap for a process struct */
static struct process*
create_process(void)
{
	struct process* proc;
	proc = (struct process*) malloc(sizeof(struct process));
	if (proc == NULL) {
		perror("create_process");
		exit(EXIT_FAILURE);
	}
	return proc;
}

/* initializes a process struct to the given id and executable name*/
static struct process*
init_process(int number, char* execName)
{
		struct process* proc = create_process();
		proc->pNumber = number;
		proc->pPid = -1;
		proc->execName = execName;
		proc->next = NULL;

		return proc;
}

/* a struct holding the number of processes and the head of a
 * process list */
struct processList {
		int count;
		struct process* head;
};

/* grabs memory from the stack for a processList*/
static struct processList*
create_list (void)
{
	struct processList* list;
	list = (struct processList*) malloc(sizeof(struct processList));
	if (list == NULL) {
		perror("processList malloc");
		exit(EXIT_FAILURE);
	}
	return list;
}
/*initializes an empty process list*/
static struct processList*
init_list (void)
{
	struct processList* list = create_list();
	list->count = 0;
	list->head = NULL;
	return list;
}

/* adds a process to the process list */
static void
add_proc_to_list (struct processList* list, struct process* proc)
{
	list->count +=1;
	if(list->head == NULL)
		list->head = proc;
	else {
		struct process* temp = list->head;
		while(temp->next != NULL)
			temp = temp->next;
		temp->next = proc;
	}
}

/* remove a process from the process list */
static void
remove_proc_from_list (struct processList* list, struct process* proc)
{
	struct process* temp = list->head;
	struct process* prev = NULL;
	list->count -= 1;

	if (list->head == proc)
		list->head = NULL;
	else {
		while(temp != proc) {
			prev = temp;
			temp = temp->next;
		}

		prev->next = temp->next;
		}
	free(temp);
}

/* given an id find the process it corresponds */
static struct process*
find_process_by_id (struct processList* list, int id)
{
	struct process* temp = list->head;

	while(temp->pNumber != id)
		temp = temp->next;

	if(temp == NULL) {
		printf("ID %d not found! Program Error! Terminating...\n", id);
		exit(1);
	}

	return temp;
}

/* Print a list of all tasks currently being scheduled.  */
static void
sched_print_tasks(void)
{
	struct process* temp = procList->head;

	printf("processes running: %d\n", procList->count);

	while(temp != NULL) {
		if (temp == current) {
			printf("Currently running: \n");
		}

		printf("process ID: %d, "
			   "process PID: %ld, "
		   	   "executable name: %s\n",
			   temp->pNumber, (long)temp->pPid, temp->execName);

			   temp = temp->next;
	}
}

/* Send SIGKILL to a task determined by the value of its
 * scheduler-specific id.
 */
static int
sched_kill_task_by_id(int id)
{
	struct process* temp = find_process_by_id(procList, id);
	kill(temp->pPid, SIGKILL);

	exceptions[0] = true;
	exceptionsID[0] = temp->pNumber;

	return -ENOSYS;
}


/* Create a new task.  */
static void
sched_create_task(char *executable)
{
	struct process* temp;
	int id = procList->count + 1;
	pid_t p;

	temp = init_process(id, executable);
	add_proc_to_list(procList, temp);

	p = fork();

	if(p < 0) {
		/* fork failed */
		perror("fork");
		exit(1);
	}

	else if (p == 0) {
		child(temp);
	}
	else {
		temp->pPid = p;
		exceptions[1] = true;
		exceptionsID[1] = temp->pNumber;

	}

}

/* Process requests by the shell.  */
static int
process_request(struct request_struct *rq)
{
	switch (rq->request_no) {
		case REQ_PRINT_TASKS:
			sched_print_tasks();
			return 0;

		case REQ_KILL_TASK:
			return sched_kill_task_by_id(rq->task_arg);

		case REQ_EXEC_TASK:
			sched_create_task(rq->exec_task_arg);
			return 0;

		default:
			return -ENOSYS;
	}
}

/*
 * SIGALRM handler
 */
static void
sigalrm_handler(int signum)
{
	printf("ALARM!!\n");
	if (signum != SIGALRM) {
		fprintf(stderr, "Internal error: Called for signum %d, not SIGALRM\n",
			signum);
		exit(1);
	}

	kill(current->pPid, SIGSTOP);
}

/*
 * SIGCHLD handler
 */
static void
sigchld_handler(int signum)
{
	pid_t p;
	int status;

	if (signum != SIGCHLD) {
		fprintf(stderr, "Internal error: Called for signum %d, not SIGCHLD\n",
			signum);
		exit(1);
	}

	/*
	 * Something has happened to one of the children.
	 * We use waitpid() with the WUNTRACED flag, instead of wait(), because
	 * SIGCHLD may have been received for a stopped, not dead child.
	 *
	 * A single SIGCHLD may be received if many processes die at the same time.
	 * We use waitpid() with the WNOHANG flag in a loop, to make sure all
	 * children are taken care of before leaving the handler.
	 */
	for(;;) {
		p = waitpid(-1, &status, WUNTRACED | WNOHANG);
		if (p < 0) {
			perror("waitpid");
			exit(1);
		}

		if (p == 0)
			break;

		explain_wait_status(p, status);

		if (WIFEXITED(status) || WIFSIGNALED(status)) {
			/* A child has died */

			/* The child died because we terminated it with SIGKILL */
			if (exceptions[0] == true) {
				struct process* temp;
				exceptions[0] = false;
				temp = find_process_by_id(procList, exceptionsID[0]);
				remove_proc_from_list(procList, temp);

				if(procList->head == NULL) {
					printf("All children finished. Exiting...\n");
					exit(EXIT_SUCCESS);
				}
			}

			/* other reasons */
			else {
				struct process* temp = current;
				current = current->next;
				remove_proc_from_list(procList, temp);

				if(procList->head == NULL) {
					printf("All children finished. Exiting...\n");
					exit(EXIT_SUCCESS);
				}
				if(current == NULL)
						current = procList->head;

				/* start alarm */
				if (alarm(SCHED_TQ_SEC) < 0) {
					perror("alarm");
					exit(1);
				}

				kill(current->pPid, SIGCONT);
			}
		}
		if (WIFSTOPPED(status)) {
				/* A child has stopped due to SIGSTOP/SIGTSTP, etc... */
				current = current->next;
				if (current == NULL)
					current = procList->head;

					/* start alarm */
				//	printf("%d\n", current->pNumber);
					if (alarm(SCHED_TQ_SEC) < 0) {
						perror("alarm");
						exit(1);
				}
			kill(current->pPid, SIGCONT);
		}
	}
}

/* Disable delivery of SIGALRM and SIGCHLD. */
static void
signals_disable(void)
{
	sigset_t sigset;

	sigemptyset(&sigset);
	sigaddset(&sigset, SIGALRM);
	sigaddset(&sigset, SIGCHLD);
	if (sigprocmask(SIG_BLOCK, &sigset, NULL) < 0) {
		perror("signals_disable: sigprocmask");
		exit(1);
	}
}

/* Enable delivery of SIGALRM and SIGCHLD.  */
static void
signals_enable(void)
{
	sigset_t sigset;

	sigemptyset(&sigset);
	sigaddset(&sigset, SIGALRM);
	sigaddset(&sigset, SIGCHLD);
	if (sigprocmask(SIG_UNBLOCK, &sigset, NULL) < 0) {
		perror("signals_enable: sigprocmask");
		exit(1);
	}
}


/* Install two signal handlers.
 * One for SIGCHLD, one for SIGALRM.
 * Make sure both signals are masked when one of them is running.
 */
static void
install_signal_handlers(void)
{
	sigset_t sigset;
	struct sigaction sa;

	sa.sa_handler = sigchld_handler;
	sa.sa_flags = SA_RESTART;
	sigemptyset(&sigset);
	sigaddset(&sigset, SIGCHLD);
	sigaddset(&sigset, SIGALRM);
	sa.sa_mask = sigset;
	if (sigaction(SIGCHLD, &sa, NULL) < 0) {
		perror("sigaction: sigchld");
		exit(1);
	}

	sa.sa_handler = sigalrm_handler;
	if (sigaction(SIGALRM, &sa, NULL) < 0) {
		perror("sigaction: sigalrm");
		exit(1);
	}

	/*
	 * Ignore SIGPIPE, so that write()s to pipes
	 * with no reader do not result in us being killed,
	 * and write() returns EPIPE instead.
	 */
	if (signal(SIGPIPE, SIG_IGN) < 0) {
		perror("signal: sigpipe");
		exit(1);
	}
}

static void
do_shell(char *executable, int wfd, int rfd)
{
	char arg1[10], arg2[10];
	char *newargv[] = { executable, NULL, NULL, NULL };
	char *newenviron[] = { NULL };

	sprintf(arg1, "%05d", wfd);
	sprintf(arg2, "%05d", rfd);
	newargv[1] = arg1;
	newargv[2] = arg2;

	raise(SIGSTOP);
	execve(executable, newargv, newenviron);

	/* execve() only returns on error */
	perror("scheduler: child: execve");
	exit(1);
}

/* Create a new shell task.
 *
 * The shell gets special treatment:
 * two pipes are created for communication and passed
 * as command-line arguments to the executable.
 */
static pid_t
sched_create_shell(char *executable, int *request_fd, int *return_fd)
{
	pid_t p;
	int pfds_rq[2], pfds_ret[2];

	if (pipe(pfds_rq) < 0 || pipe(pfds_ret) < 0) {
		perror("pipe");
		exit(1);
	}

	p = fork();
	if (p < 0) {
		perror("scheduler: fork");
		exit(1);
	}

	if (p == 0) {
		/* Child */
		close(pfds_rq[0]);
		close(pfds_ret[1]);
		do_shell(executable, pfds_rq[1], pfds_ret[0]);

		/* Unreachable */
		assert(0);
	}
	/* Parent */
	close(pfds_rq[1]);
	close(pfds_ret[0]);
	*request_fd = pfds_rq[0];
	*return_fd = pfds_ret[1];

	return p;
}

static void
shell_request_loop(int request_fd, int return_fd)
{
	int ret;
	struct request_struct rq;

	/*
	 * Keep receiving requests from the shell.
	 */
	for (;;) {
		if (read(request_fd, &rq, sizeof(rq)) != sizeof(rq)) {
			perror("scheduler: read from shell");
			fprintf(stderr,
				"Scheduler: giving up on shell request processing.\n");
			break;
		}

		signals_disable();
		ret = process_request(&rq);
		signals_enable();

		if (write(return_fd, &ret, sizeof(ret)) != sizeof(ret)) {
			perror("scheduler: write to shell");
			fprintf(stderr,
				 "Scheduler: giving up on shell request processing.\n");
			break;
		}
	}
}

static void
child(struct process* proc)
{
	char *execName = proc->execName;
	char *newargv[] = { execName, NULL, NULL, NULL };
	char *newenviron[] = { NULL };
	raise(SIGSTOP);
	execve(execName, newargv, newenviron);

	/* Unreachable */
	perror("execve:");
	exit(1);
}


int main(int argc, char *argv[])
{
	int nproc;
	pid_t p;
	int i;

	/* Two file descriptors for communication with the shell */
	static int request_fd, return_fd;

	/* Initialize the processes list */
	procList = init_list();

	/* Create a process for each of the given arguments */
	for (i=1; i < argc; i++) {
		current = init_process(i, argv[i]);
		add_proc_to_list(procList, current);
	}

	/* Create the shell. */
	p = sched_create_shell(SHELL_EXECUTABLE_NAME, &request_fd, &return_fd);

	/* Add the shell to the scheduler's tasks */
	current = init_process(i, SHELL_EXECUTABLE_NAME);
	current->pPid = p;
	add_proc_to_list(procList, current);


	nproc = argc - 1; /* number of processes goes here */

	current = procList->head;
	for (i = 0; i < nproc; i++) {
		p = fork();
		if (p < 0) {
			/* fork failed */
			perror("fork");
			exit(1);
		}
		else if (p == 0) {
			child(current);
		}
		else  if (p > 0) {
			current->pPid = p;
			current = current->next;
		}

	}

	/* Wait for all children to raise SIGSTOP before exec()ing. */
	wait_for_ready_children(nproc + 1);

	/* Install SIGALRM and SIGCHLD handlers. */
	install_signal_handlers();

	if (nproc == 0) {
		fprintf(stderr, "Scheduler: No tasks. Exiting...\n");
		exit(1);
	}

	current = procList->head;

	/* start alarm */
	if (alarm(SCHED_TQ_SEC) < 0) {
		perror("alarm");
		exit(1);
	}

	/* enable first process */
	pid_t pid = current->pPid;
	kill(pid, SIGCONT);


	shell_request_loop(request_fd, return_fd);

	/* Now that the shell is gone, just loop forever
	 * until we exit from inside a signal handler.
	 */
	while (pause())
		;

	/* Unreachable */
	fprintf(stderr, "Internal error: Reached unreachable point\n");
	return 1;
}
