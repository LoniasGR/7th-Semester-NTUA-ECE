#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main (int argc, char **argv)
{
    if (argc < 2) {
    printf("Not enough input files!\n"); 
    printf("Usage: ./fconc infile1 infile2 [outfile (default: focnc.out)]");
    return(EXIT_FAILURE);
    }
    return 0;
}
