#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>

#include "tree.h"
#include "proc-common.h"

void fork_procs(struct tree_node *root)
{
    printf("PID = %ld, name %s, starting...\n",
            (long)getpid(), root->name);
    change_pname(root->name);
    pid_t pid;
    pid_t pid_arr[root->nr_children];
    int i;
    int status;
    for (i=0; i < root->nr_children; i++) {
        pid = fork();
        if (pid < 0) {
            perror("fork");
            exit(EXIT_FAILURE);
        }
        if (pid == 0) {
            struct tree_node *child;
            child = ((root->children) + i);
            fork_procs(child);
        }
        if (pid > 0) 
            pid_arr[i] = pid;
    }

 if(root->nr_children!=0)   wait_for_ready_children(root->nr_children);
    printf("PID = %ld, name = %s is awake\n",
            (long)getpid(), root->name);       
    raise(SIGSTOP);
    for (i = 0; i < root->nr_children; i++) {
        kill(pid_arr[i], SIGCONT);
        waitpid(-1, &status, 0);
        explain_wait_status(pid_arr[i], status);
    }
    exit(0);
}

int main(int argc, char *argv[])
{
    pid_t pid;
    int status;
    struct tree_node *root;

    if (argc < 2){
        fprintf(stderr, "Usage: %s <tree_file>\n", argv[0]);
        exit(1);
    }

    /* Read tree into memory */
    root = get_tree_from_file(argv[1]);

    /* Fork root of process tree */
    pid = fork();
    if (pid < 0) {
        perror("main: fork");
        exit(1);
    }
    if (pid == 0) {
        /* Child */
        fork_procs(root);
        exit(1);
    }
    wait_for_ready_children(1);
    show_pstree(pid);
    kill(pid, SIGCONT);
    wait(&status);
    explain_wait_status(pid, status);

    return 0;
}
