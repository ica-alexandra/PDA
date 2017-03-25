#include<iostream>
#include<stdlib.h>
#include<time.h>
#define SIZE 100

using namespace std;


int main()
{
	int first[SIZE][SIZE], second[SIZE][SIZE], rez[SIZE][SIZE];
	int i, j;
	volatile int flag = 0;

	srand(time(NULL));

	for (i = 0; i < SIZE; i++)
		for (j = 0; j < SIZE; j++) {
			first[i][j] = rand() % 100;
		}

	for (i = 0; i < SIZE; i++)
		for (j = 0; j < SIZE; j++) {
			second[i][j] = rand() % 100;
		}

	for (i = 0; i < SIZE; i++) {
		for (j = 0; j < SIZE; j++) {
			cout << first[i][j] << " ";
		}
		cout << "\n";
	}

	cout << "\n";
	cout << "\n";

	for (i = 0; i < SIZE; i++) {
		for (j = 0; j < SIZE; j++) {
			cout << second[i][j] << " ";
		}
		cout << "\n";
	}

	cout << "\n";
	cout << "\n";

	for (i = 0; i < SIZE; i++)
		for (j = 0; j < SIZE; j++) {
			rez[i][j] = 0;
		}

	#pragma omp parallel
	{
		cout <<"Thread id: "<< _threadid << endl;
		for (i = 0; i < SIZE; i++)
			for (j = 0; j < SIZE; j++)
				for (int k = 0; k < SIZE; k++)
				{
					rez[i][j] += first[i][k] * second[k][j];
				}
	}
	

	for (i = 0; i < SIZE; i++) {
		for (j = 0; j < SIZE; j++) {
			cout << rez[i][j] << " ";
		}
		cout << "\n";
	}
	getchar();
	return 0;
	
}


