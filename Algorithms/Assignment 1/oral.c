#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

void check_args(int num_of_args)
{
    if (num_of_args != 2) {
        printf("Usage: ./oral input.txt\n");
        exit(EXIT_FAILURE);
    }
}

FILE* open_file(char *file_name)
{
    FILE *fp;
    fp = fopen(file_name, "r");
    if (fp == NULL) {
        printf("Error with file: %s\nExiting...", file_name);
        exit(EXIT_FAILURE);
    }
    return fp;
}

long int read_from_file(FILE *fp)
{
    long int number;
    fscanf(fp, "%ld", &number);
    return number;
}

long long int* allocate_memory_for_array (int number_of_slots)
{
    long long int *array;
    array = (long long int*) malloc(number_of_slots * sizeof(long long int));
    if (array == NULL) {
        printf("Memmory allocation error.\nExiting");
        exit(EXIT_FAILURE);
    }
    return array;
}

int main(int argc, char **argv)
{
    printf("Size of long long int is: %ld\n", sizeof(long long int));
    FILE *file_pointer = stdin;
    long long int number_of_examiners;
    long long int position_in_line;
    long long int *examiners_time;
    long long int current_time = 1;
    long long int previous_time = 0;
    long long int i;
    long long int my_examiner;
    long long int examined_so_far;
    bool found = false;
    long long int mid_time;


    check_args(argc);
    file_pointer = open_file(argv[1]);
    number_of_examiners = read_from_file(file_pointer);
    position_in_line = read_from_file(file_pointer);
    examiners_time = allocate_memory_for_array(number_of_examiners);
    for (i = 0; i < number_of_examiners; i++)
        examiners_time[i] = read_from_file(file_pointer);
    if (number_of_examiners >= position_in_line) {
        current_time = 1;
        my_examiner = position_in_line;
        found = true;
    }
    else {
            for (current_time = 1; ;current_time*=2) {
                examined_so_far = 0;
                    for(i = 0; i < number_of_examiners; i++) {
                        if (examiners_time[i] <= current_time) {
                            examined_so_far += current_time/examiners_time[i];
                        }
                    }
                    if (examined_so_far >= position_in_line)
                            break;
                    else
                        previous_time = current_time;
            }
    }
    if (!found) {
        while((current_time - previous_time) != 1) {
            mid_time = previous_time + (current_time - previous_time)/2;
            examined_so_far = 0;
            for(i = 0; i < number_of_examiners; i++) {
                if (examiners_time[i] <= mid_time)
                    examined_so_far += mid_time/examiners_time[i];
            }
            if (examined_so_far - position_in_line > 0 )
                current_time = mid_time;
            else
                previous_time = mid_time;
            printf("%lld %lld %lld\n",previous_time, mid_time, current_time);
            printf("%lld\n", examined_so_far );
        }
        //printf("HELLO WORLD\n");
        examined_so_far = number_of_examiners;
        for(i = 0; i < number_of_examiners; i++) {
            if (examiners_time[i] <= mid_time)
                examined_so_far += mid_time/examiners_time[i];
        }
        if (examined_so_far >= position_in_line) {
            //  printf("HELLO ME\n");
            while (examined_so_far >= position_in_line) {
                //printf("HELLO YOU\n");
                examined_so_far = number_of_examiners;
                for(i = 0; i < number_of_examiners; i++) {
                    if (examiners_time[i] <= mid_time)
                        examined_so_far += mid_time/examiners_time[i];
                }
                //printf("MID TIME %lld\n", mid_time);
                mid_time--;
            }
            mid_time++;
            mid_time++;
            //printf("EXAMINED SO FAR:%lld\n", examined_so_far);
            long long int examiner_status = -1;
            for(i = 0; i < number_of_examiners; i++) {
                //printf("HELLO ALL\n");
                if (examiners_time[i] <= mid_time) {
                    //printf("%lld\n", mid_time % examiners_time[i]);
                    if ((mid_time % examiners_time[i]) == 0
                        && examiner_status == -1) {
                            //printf("HAI \n");
                            examined_so_far++;
                        if (examined_so_far >= position_in_line) {
                        my_examiner = i;
                        examiner_status = 1;
                    }
                    }
                }
            }
        }
        else {
            printf("HELLO EARTH\n");
                while (examined_so_far < position_in_line) {
                mid_time++;
                //examined_so_far = number_of_examiners;
                examined_so_far = 0;
                for(i = 0; i < number_of_examiners; i++) {
                    if (examiners_time[i] <= mid_time) {
                        examined_so_far += mid_time/examiners_time[i];
                    }
                }
            }
            long long int examiner_status = -1;
            for(i = 0; i < number_of_examiners; i++) {
                if (examiners_time[i] <= mid_time) {
                    if (mid_time%examiners_time[i] == 0
                        && examiner_status == -1) {
                        my_examiner = i;
                        examiner_status = 1;
                    }
                }
            }
        }
    }
    if (!found) {
    //printf("NOT\n");
    //printf("%lld\n",my_examiner );
    printf("%lld %lld\n", my_examiner+1, mid_time+examiners_time[my_examiner]);
    //printf("NOT FOUND\n");
    }
if (found)
    printf("%lld %lld\n", my_examiner, current_time+examiners_time[my_examiner]);
    free(examiners_time);
    return 0;
}
