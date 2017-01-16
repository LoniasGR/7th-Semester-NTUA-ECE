/*
 * mandel.c
 *
 * A program to draw the Mandelbrot Set on a 256-color xterm.
 *
 */

#include <stdio.h>
#include <unistd.h>
#include <assert.h>
#include <string.h>
#include <math.h>
#include <errno.h>
#include <stdlib.h>
#include <pthread.h> /*for tread creation*/
#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h> /* for semaphore use*/
#include <signal.h>	/*for signal use*/

#include "mandel-lib.h"

#define MANDEL_MAX_ITERATION 100000
#define fd 1

#define perror_pthread(ret, msg) \
	do { errno = ret; perror(msg); } while (0)

/***************************
 * Compile-time parameters *
 ***************************/

/*
 * Output at the terminal is is x_chars wide by y_chars long
*/

typedef struct {
	pthread_t thread;
	int thread_number;
	sem_t semaphore;
} my_threads;

int y_chars = 50;
int x_chars = 90;
int NTHREADS;
my_threads *thread_arr;



/*
 * The part of the complex plane to be drawn:
 * upper left corner is (xmin, ymax), lower right corner is (xmax, ymin)
*/
double xmin = -1.8, xmax = 1.0;
double ymin = -1.0, ymax = 1.0;

/*
 * Every character in the final output is
 * xstep x ystep units wide on the complex plane.
 */
double xstep;
double ystep;



void reset_color_and_exit (int sig) {
	reset_xterm_color(1);
	exit(EXIT_FAILURE);
}
/*
 * This function computes a line of output
 * as an array of x_char color values.
 */
void compute_mandel_line(int line, int color_val[])
{
	/*
	 * x and y traverse the complex plane.
	 */
	double x, y;

	int n;
	int val;

	/* Find out the y value corresponding to this line */
	y = ymax - ystep * line;

	/* and iterate for all points on this line */
	for (x = xmin, n = 0; n < x_chars; x+= xstep, n++) {

		/* Compute the point's color value */
		val = mandel_iterations_at_point(x, y, MANDEL_MAX_ITERATION);
		if (val > 255)
			val = 255;

		/* And store it in the color_val[] array */
		val = xterm_color(val);
		color_val[n] = val;
	}
}

/*
 * This function outputs an array of x_char color values
 * to a 256-color xterm.
 */
void output_mandel_line(int color_val[])
{
	int i;

	char point ='@';
	char newline='\n';

	for (i = 0; i < x_chars; i++) {
		/* Set the current color, then output the point */
		set_xterm_color(fd, color_val[i]);
		if (write(fd, &point, 1) != 1) {
			perror("compute_and_output_mandel_line: write point");
			exit(1);
		}
	}

	/* Now that the line is done, output a newline character */
	if (write(fd, &newline, 1) != 1) {
		perror("compute_and_output_mandel_line: write newline");
		exit(1);
	}
}

void *compute_and_output_mandel_line(void *arg)
{
	/*
	 * A temporary array, used to hold color values for the line being drawn
	 */
	int color_val[x_chars];
	int thread = *((int *)arg);
	int line;
	for(line = thread; line < y_chars; line += NTHREADS)  {
			compute_mandel_line(line, color_val);
			sem_wait(&thread_arr[thread].semaphore);
			output_mandel_line(color_val);
			int ret = sem_post(&thread_arr[(thread + 1) % NTHREADS].semaphore);
			if (ret) {
				perror_pthread(ret, "sem_post");
				exit(1);
			}
	}
	return NULL;
}

int main(int argc, char *argv[])
{
	signal(SIGINT, reset_color_and_exit);
    if (argc != 2) {
        printf("Use: ./mandel NTHREADS \n");
        return -1;
    }

	int ret;
	int thread;
    NTHREADS = atoi(argv[1]);

	if (NTHREADS < 1 || NTHREADS > y_chars) {
		printf("Threads need to be between 1 and 50. \n");
		return -1;
	}

	thread_arr = (my_threads *)calloc(NTHREADS, sizeof(my_threads));

	xstep = (xmax - xmin) / x_chars;
	ystep = (ymax - ymin) / y_chars;

	sem_init(&thread_arr[0].semaphore, 0, 1);

	for (thread = 1; thread < NTHREADS; thread++) {
		sem_init(&thread_arr[thread].semaphore, 0, 0);
	}

	/*
	 * draw the Mandelbrot Set, one line at a time.
	 * Output is sent to file descriptor '1', i.e., standard output.
	 */
	for (thread = 0; thread < NTHREADS; thread++) {
		thread_arr[thread].thread_number = thread;
		ret = pthread_create(&(thread_arr[thread].thread), NULL,
			compute_and_output_mandel_line, &thread_arr[thread].thread_number);
			if (ret) {
				perror_pthread(ret, "pthread_create");
				exit(1);
			}
	}

	for (thread = 0; thread < NTHREADS; thread++) {
		ret = pthread_join(thread_arr[thread].thread, NULL);
		if (ret)
			perror_pthread(ret, "pthread_join");
	}

	reset_xterm_color(1);
	return 0;
}
