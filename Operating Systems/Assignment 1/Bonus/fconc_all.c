#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>

#define TEMP_FILE "/tmp/fconc.tmp"
#define MAX_READ 100
#define FILEPERMS S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH
#define WRITEPERMS O_WRONLY | O_CREAT | O_TRUNC

void check_input_files_number (int argc)
{
    if (argc < 3) {
        printf("Usage: ./fconc infile1 [infile2 infile3 ..] outfile\n");
        exit(EXIT_FAILURE);
    }
}
int open_input_file (char *FileName)
{
    int inFile = open(FileName, O_RDONLY);
    if (inFile == -1) {
        perror(FileName);
        close(inFile);
        exit(EXIT_FAILURE);
    }
    else
        return inFile;
}

int open_output_file (int argc, char **argv)
{
        int outFile = open(argv[argc - 1], WRITEPERMS, FILEPERMS);
        if (outFile == -1) {
            perror(argv[argc - 1]);
            close(outFile);
            exit(EXIT_FAILURE);
        }
        else
            return outFile;
}

void closeFile (int File, char *inFileName)
{
    if (close(File) == -1) {
        perror(inFileName);
        exit(EXIT_FAILURE);
    }
}

void doWrite (int outFile, char *buff, int len)
{
    ssize_t bytes_written = write(outFile, buff, len);
    ssize_t baw = 0; //baw stands for bytes actually written
    if (bytes_written == -1) {
        perror("Output File");
        close(outFile);
        exit(EXIT_FAILURE);
    }
    else if (bytes_written != len) {
        baw += bytes_written;
        while (len != baw) {
            baw += write(outFile,buff+baw, len - baw);
            if (bytes_written == -1) {
                perror("Output File");
                close(outFile);
                exit(EXIT_FAILURE);
            }
        }
    }
}


void write_file (int outFile, char *inFileName)
{
    int inFile = open_input_file(inFileName);
    char buffer[MAX_READ+1];
    ssize_t bytesRead = 1;
    while (bytesRead != 0) {
        bytesRead = read(inFile, buffer, MAX_READ);
        if (bytesRead == -1) {
            perror(inFileName);
            close(inFile);
            close(outFile);
            exit(EXIT_FAILURE);
        }
        else {
            buffer[bytesRead] = '\0';
            doWrite (outFile, buffer, bytesRead);
        }
    }
    closeFile(inFile, inFileName);
}

void create_intermediate_file (int argc, char **argv)
/* Creates an intermidiate file to save the contents of the input 
     * files. This is especially helpful when we want to have a file
     * both as input and output.*/
{
    int temp_file = open(TEMP_FILE, WRITEPERMS, FILEPERMS);
    for (int i=1; i <= argc - 2 ;i++)
        write_file(temp_file, argv[i]);
     closeFile(temp_file, TEMP_FILE);
}

int main (int argc, char **argv)
{
    int outFile;
    create_intermediate_file(argc, argv);
    outFile = open_output_file(argc, argv);
    write_file(outFile, TEMP_FILE);
    remove(TEMP_FILE);
    closeFile(outFile, argv[argc - 1]);
    return 0;
}
