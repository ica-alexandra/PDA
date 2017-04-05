#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include<time.h>
#define SIZE 100
#define KEY 95


int main(int argc, char *argv[])
{
	int rank, size, segmentSize, index, found[SIZE], finalFound[SIZE], arr[SIZE], segment[SIZE];

	srand(time(NULL) + 1);

	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);

	if (rank == 0) {
		printf("Array elements are:\n");
		for (int i = 0; i < SIZE; i++) {
			arr[i] = rand() % 100;
			printf("%d, ", arr[i]);
			finalFound[i] = 0;
		}
	}

	segmentSize = SIZE / size;
	if (SIZE % size != 0)
		++segmentSize;

	MPI_Scatter(arr, segmentSize, MPI_INT, segment, segmentSize, MPI_INT, 0, MPI_COMM_WORLD);

	index = 0;
	for (int i = 0; i < segmentSize; i++)
		found[i] = 0;

	for (int i = 0; i < segmentSize; i++) {
		if (segment[i] == KEY) {
			found[index] = i;
			index++;
		}
	}

	MPI_Gather(found, index, MPI_INT, finalFound, SIZE, MPI_INT, 0, MPI_COMM_WORLD);

	if (rank == 0) {
		if (finalFound[0] == 0)
			printf("\nThe number wasn`t found\n");
		else {
			int poz = 0;
			printf("Number found on the following positions: ");
			while (finalFound[poz] != 0)
				printf("%d, ", finalFound[poz]);
		}
	}


	MPI_Finalize();

}

