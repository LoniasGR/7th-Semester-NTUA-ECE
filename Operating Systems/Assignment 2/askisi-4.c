#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <string.h>

#include "tree.h"
#include "proc-common.h"

#define SLEEP_SECONDS 1

int read_from_pipe (int file)
{
    FILE *stream;
    int number;
    stream = fdopen(file, "r");
    fscanf(stream,"%d", &number);
    fclose(stream);
    return number;
}

void write_to_pipe (int file, int number)
{
    FILE *stream;
    stream = fdopen(file, "w");
    fprintf(stream, "%d", number);
    fclose(stream);
}

int calculate (struct tree_node *root, int *mypipe) 
{
    change_pname(root->name);
    printf("PID = %ld, name %s, starting...\n",
            (long)getpid(), root->name);      
    pid_t pid;
    int i,  result = 0;
    int side_pipe[2][2];
    if (pipe(side_pipe[0])) {
        perror("pipe_1");    
        exit(EXIT_FAILURE);
    }
    if (pipe(side_pipe[1])) {
        perror("pipe_2");    
        exit(EXIT_FAILURE);
    }

    for (i=0; i < root->nr_children; i++) {
        pid = fork();
        if (pid < 0) {
            perror("fork");
            exit(EXIT_FAILURE);
        }
        if (pid == 0) {
            struct tree_node *child;
            child = ((root->children) + i);
            calculate(child, side_pipe[i]);
        }
    }

    if (i == 0) {
        printf("PID = %ld, name = %s going to sleep\n",
                (long)getpid(), root->name);
       result =  atoi(root->name);       
    }
    else {
        printf("PID = %ld, name = %s waiting\n",
                (long)getpid(), root->name);
        close(side_pipe[0][1]);
        int num_1 = read_from_pipe(side_pipe[0][0]);
        close(side_pipe[1][1]);
        int num_2 = read_from_pipe(side_pipe[1][0]);     
        if (strcmp(root->name,"+") == 0)  {
            result = num_1 + num_2;          
        }
        else if (strcmp(root->name, "*") == 0) {
            result = num_1 * num_2;
        }
    }
    close(mypipe[0]);
    write_to_pipe(mypipe[1], result);
    printf("PID = %ld, name = %s terminating\n",
            (long)getpid(), root->name);
    exit(EXIT_SUCCESS);

}

int main(int argc, char *argv[])
{
    struct tree_node *root;
    pid_t pid;
    int main_pipe[2];
    int result;
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <input_tree_file>\n\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    root = get_tree_from_file(argv[1]);
    if (pipe(main_pipe))
        printf("ERROR");
    pid = fork();
    if (pid < 0) {
        perror("main:fork");
        exit(EXIT_FAILURE);
    }
    if (pid == 0) {
        calculate(root, main_pipe);
    }
    //show_pstree(pid);
    close(main_pipe[1]);
    result = read_from_pipe(main_pipe[0]);
    printf("Final Result is: %d \n", result);
    return 0;
}
