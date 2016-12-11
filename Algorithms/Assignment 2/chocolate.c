#include <stdio.h>
#include <limits.h>
#define max(a,b) (((a) < (b)) ? (b) : (a))

int maxProfit (int* price, int days, int transactions)
{
	/*profit [transactions][days] */
	int profit[2][days + 1];
	int i, j;
	int index = 1;
	for (i = 0; i <=1; i++)
		profit[i][0] = 0;

	for (j=0; j<=days; j++)
		profit[0][j] = 0;

	for(i=1; i <=transactions; i++) {
		int prevDiff = INT_MIN;
		if (index == 1) {
			for(j=1; j<days; j++) {
				prevDiff = max(prevDiff,
					profit[0][j-1] - price[j-1]);
				profit[1][j] = max(profit[1][j-1],
					price[j] + prevDiff);
				}
			index = 0;
			}
		else {
			for(j=1; j<days; j++) {
				prevDiff = max(prevDiff,
					profit[1][j-1] - price[j-1]);
				profit[0][j] = max(profit[0][j-1],
					price[j] + prevDiff);
			}
			index = 1;
		}
	}
	if (!(index == 1))
		return profit[1][days-1];
	else
		return profit[0][days-1];
}

int main (int argc, char **argv) {
	FILE *fp;
	if (argc == 1) {
	fp = stdin;
	}
	else {
	 fp = fopen(argv[1], "r");
	}
	int days, transactions;
	int price[10000];
	int i;
	fscanf(fp, "%d %d\n", &days, &transactions);
	for (i=0; i < days; i++) {
		fscanf(fp, "%d", &price[i]);
	}
	//fclose(fp);
	int result = maxProfit(price, days, transactions);
	printf("%d\n", result);
	return 0;
}
