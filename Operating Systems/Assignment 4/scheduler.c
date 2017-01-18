#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>
#include <assert.h>

#include <sys/wait.h>
#include <sys/types.h>

#include "proc-common.h"
#include "request.h"

/* Compile-time parameters. */
#define SCHED_TQ_SEC 2                /* time quantum */
#define TASK_NAME_SZ 60               /* maximum size for a task's name */


/************* DEFINITIONS ***********************************/

struct processList;
struct process;

/*************************************************************/


/************* GLOBAL VARIABLES ******************************/

static struct processList* procList;
static struct process* current;

/*************************************************************/

/* a process has a pNumber given by us, a PID given by the kernel and
 * the name of the executable that is running.*/
 struct process {
		int pNumber;
		pid_t pPid;
		char* execName;
		struct process* next;
};

/* allocates space in the heap for a process struct */
static struct process* create_process(void)
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
static struct process* init_process(int number, char* execName)
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
static struct processList* create_list (void)
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
static struct processList* init_list (void)
{
	struct processList* list = create_list();
	list->count = 0;
	list->head = NULL;
	return list;
}

/* adds a process to the process list */
static void add_proc_to_list (struct processList* list, struct process* proc)
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
static void remove_proc_from_list (struct processList* list, struct process* proc)
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


/*
 * SIGALRM handler
 */
static void sigalrm_handler(int signum)
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
static void sigchld_handler(int signum)
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

		if (WIFSTOPPED(status)) {
			/* A child has stopped due to SIGSTOP/SIGTSTP, etc... */
			current = current->next;
			if (current == NULL)
				current = procList->head;

			/* start alarm */
			if (alarm(SCHED_TQ_SEC) < 0) {
				perror("alarm");
				exit(1);
			}
			kill(current->pPid, SIGCONT);
		}
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
child(void)
{
	char *newargv[] = { current->execName, NULL, NULL, NULL };
	char *newenviron[] = { NULL };
	execve(current->execName, newargv, newenviron);

	/* Unreachable */
	perror("execve:");
	exit(1);
}

int main(int argc, char *argv[])
{
	int nproc;
	pid_t p;
	int i;

	procList = init_list();
	for (i=1; i < argc; i++) {
		printf("i is %d\n", i);
		current = init_process(i, argv[i]);
		add_proc_to_list(procList, current);
	}

	nproc = argc - 1; /* number of processes goes here */

	current = procList->head;
	for (i = 0; i < nproc; i++) {
		printf("i now is %d from proc %ld\n", i, (long)getpid());
		p = fork();
		if (p < 0) {
			/* fork failed */
			perror("fork");
			exit(1);
		}
		else if (p == 0) {
			raise(SIGSTOP);
			child();
		}
		else  if (p > 0) {
			current->pPid = p;
			current = current->next;
		}

	}

	/* Wait for all children to raise SIGSTOP before exec()ing. */
	wait_for_ready_children(nproc);


	printf("ALL CHILDREN READY!\n");

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
	pid_t pid = current->pPid;
	kill(pid, SIGCONT);
	printf("And now we wait....\n");

	/* loop forever  until we exit from inside a signal handler. */
	while (pause())
		;

	/* Unreachable */
	fprintf(stderr, "Internal error: Reached unreachable point\n");
	return 1;
}
