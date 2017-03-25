#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include<time.h>
#define SIZE 100
#define KEY 95


int main(int argc, char *argv[])
{
	int rank, size, segmentSize, startPos, endPos, index = 0;
	int arr[SIZE];
	int foundPos[SIZE];
	bool found = false;
	
	srand(time(NULL) + 1);

	for (int i = 0; i < SIZE; i++) {
		arr[i] = rand() % 100;
	}
	
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);

	//printf("World size: %d\n", size);
	
	segmentSize = SIZE / size;
	if (SIZE % size != 0)
		++segmentSize;
	//printf("Segment size: %d\n", segmentSize);
	startPos = rank * segmentSize;
	endPos = startPos + segmentSize - 1;
	//printf("Process %d: start position %d ; end position %d \n Elements: ", rank, startPos, endPos);
	printf("Process %d elements: ", rank, endPos);
	for (int i = startPos; i <= endPos; i++) {
		printf("%d ", arr[i]);
		if (arr[i] == KEY) {
			found = true;
			//foundPos[index++] = i + 1 - startPos;
			foundPos[index++] = i + 1;
		}
	}	

	if (!found)
		printf("\nNot found");
	else {
		printf("\nThe element with the value %d was found on the following position(s): ", KEY);
		for (int j = 0; j < index; j++)
			printf("%d ", foundPos[j]);
		found = false;
	}

	MPI_Finalize();

}

