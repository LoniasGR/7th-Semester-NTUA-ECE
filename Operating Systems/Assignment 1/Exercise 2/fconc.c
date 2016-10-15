#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#define MAX_READ 100
#define FILEPERMS S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH
#define WRITEPERMS O_WRONLY | O_CREAT | O_TRUNC

void check_input_files_number (int argc)
{
    if (argc < 3) {
        printf("Not enough input files!\n"); 
        printf("Usage: ./fconc infile1 infile2 [outfile (default: focnc.out)]\n");
        exit(EXIT_FAILURE);
    }
    else if (argc > 4) {
        printf("Too many arguments given!\n");
        printf("Usage: ./fconc infile1 infile2 [outfile (default: focnc.out)]\n");
        exit(EXIT_FAILURE);
    }
}
int open_input_file (char *FileName)
{
    int inFile = open(FileName, O_RDONLY);
    if (inFile == -1) {
        perror(FileName);
        exit(EXIT_FAILURE);
    }
    else 
        return inFile;
}

int open_output_file (int argc, char **argv)
{
    
    if (argc == 4) {
        int outFile = open(argv[3], WRITEPERMS, FILEPERMS);
        if (outFile == -1) {
            perror(argv[3]);
            exit(EXIT_FAILURE);
        }
        else 
            return outFile;
    }
    else {
        int outFile = open("fconc.out", WRITEPERMS, FILEPERMS);
        if (outFile == -1) {
            perror("fconc.out");
            exit(EXIT_FAILURE);
        }
        else
            return outFile;
    }        
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
    if (bytes_written == -1) {
        perror("Output File");
        exit(EXIT_FAILURE);
    }
    else if (bytes_written != len) {
        perror("Output File");
        exit(EXIT_FAILURE);
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
            exit(EXIT_FAILURE);
        }
        else {
            buffer[bytesRead] = '\0';
            doWrite (outFile, buffer, bytesRead);
        }
    }
    closeFile(inFile, inFileName);
}

int main (int argc, char **argv)
{
    check_input_files_number(argc);
    int outFile;
    outFile = open_output_file(argc, argv);
    write_file(outFile,argv[1]);
    write_file(outFile,argv[2]);
    closeFile(outFile, argv[3]);
    return 0;
}
