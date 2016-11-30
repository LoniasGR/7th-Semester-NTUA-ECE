#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <sys/types.h>
#include <sys/wait.h>

#include "proc-common.h"

#define SLEEP_PROC_SEC  10
#define SLEEP_TREE_SEC  3

/*
 * Create this process tree:
 * A-+-B---D
 *   `-C
 */
void fork_procs(void)
{
    /*
     * initial process is A.
     */
    printf("A just created \n");
    change_pname("A");
    pid_t pid;
    pid = fork();
    if (pid < 0) {
        perror ("A:fork");
        exit(1);
    }
    if (pid == 0) {
        printf("B just created \n");
        change_pname("B");
        pid = fork();
        if (pid < 0) {
            perror("B:fork");
            exit(1);
        }
        if (pid == 0) {
            printf("D just created \n");
            change_pname("D");
            printf("D: Sleeping...\n");
            sleep(SLEEP_PROC_SEC);
            printf("D: Exiting...\n");
            exit(13);
        }
        if (pid > 0) {
            int status;
            wait(&status);
            explain_wait_status(pid,status);
            printf("B: Exiting...\n");
            exit(19);
        }
    }
    if (pid > 0) {
        pid_t pid_2 = fork();
        if (pid_2 < 0) {
            perror("A: fork");
            exit(1);
        }
        if (pid_2 == 0) {
            printf("C just created \n");
            change_pname("C");
            printf("C: Sleeping...\n");
            sleep(SLEEP_PROC_SEC);
            printf("C: Exiting...\n");
            exit(17);
        }
        if (pid_2 > 0) {  
            int status;
            waitpid(pid,&status,0);
            explain_wait_status(pid,status);
            waitpid(pid_2,&status,0);
            explain_wait_status(pid_2,status);
            printf("A: Exiting...\n");
            exit(16);
        }
    }
}

int main(void)
{
    pid_t pid;
    int status;
    /* Fork root of process tree */
    pid = fork();
    if (pid < 0) {
        perror("main: fork");
        exit(1);
    }
    if (pid == 0) {
        /* Child */
        fork_procs();
        exit(1);
    }
    sleep(SLEEP_TREE_SEC);
    show_pstree(pid);
    pid = wait(&status);
    explain_wait_status(pid, status);

    return 0;
}
