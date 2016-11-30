#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

#include "tree.h"
#include "proc-common.h"

#define SLEEP_TREE_SECONDS 1
#define SLEEP_NODE_SECONDS 3

void dfs_pid (struct tree_node *root) {
    change_pname(root->name);
    pid_t pid;
    int i;
    printf("PID = %ld, name %s, starting...\n",
            (long)getpid(), root->name);
    for (i=0; i < root->nr_children; i++) {
        pid = fork();
        if (pid < 0) {
            perror("fork");
            exit(EXIT_FAILURE);
        }
        if (pid == 0) {
            struct tree_node *child;
            child = ((root->children) + i);
            dfs_pid(child);
        }
    }
    if (i == 0) {
        printf("PID = %ld, name = %s going to sleep\n",
                (long)getpid(), root->name);
        sleep(SLEEP_NODE_SECONDS);
    }
    else {
        int status;
        printf("PID = %ld, name = %s waiting\n",
                (long)getpid(), root->name);
        for (i = 0; i < root-> nr_children; i++) {
        wait(&status);
        explain_wait_status(pid, status);        
        }
    }
   printf("PID = %ld, name = %s terminating\n",
            (long)getpid(), root->name);
    exit(EXIT_SUCCESS);

}

int main(int argc, char *argv[])
{
    struct tree_node *root;
    pid_t pid;
    int status;
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <input_tree_file>\n\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    root = get_tree_from_file(argv[1]);
    pid = fork();
    if (pid < 0) {
        perror("main:fork");
        exit(EXIT_FAILURE);
    }
    if (pid == 0) {
        dfs_pid(root);
    }
    sleep(SLEEP_TREE_SECONDS);   
    show_pstree(pid);
    wait(&status);
    explain_wait_status(pid, status);
    return 0;
}
