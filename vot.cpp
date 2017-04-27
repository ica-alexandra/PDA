#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char** argv) {

	int rank, myVote = -2, currVote = -1;
	int world_size, sender, receiver;
	int leader = -100;

	MPI_Init(NULL, NULL);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);

	if (rank == 0)
		sender = world_size - 1;
	else sender = rank - 1;

	if (rank == world_size - 1)
		receiver = 0;
	else receiver = rank + 1;

	
	srand(time(NULL) + rank);
	myVote = rand() % 100 + 1;

	MPI_Barrier(MPI_COMM_WORLD);

	printf("Process %d has voted as: %d \n", rank, myVote);

	if (rank != 0) {
		MPI_Recv(&currVote, 1, MPI_INT, sender, 0, MPI_COMM_WORLD, MPI_STATUSES_IGNORE);
		printf("Process %d received vote %d from process %d\n", rank, currVote, sender);

		if (currVote > myVote) {
			leader = currVote;
			MPI_Send(&currVote, 1, MPI_INT, receiver, 0, MPI_COMM_WORLD);
			printf("Process %d sent vote %d to process %d\n", rank, currVote, receiver);
		}
		else {
			MPI_Send(&myVote, 1, MPI_INT, receiver, 0, MPI_COMM_WORLD);
			printf("Process %d sent vot %d to process %d\n", rank, myVote, receiver);
		}
	}
	else {
		MPI_Send(&myVote, 1, MPI_INT, receiver, 0, MPI_COMM_WORLD);
		printf("Process %d sent vote %d  to process %d\n", rank, myVote, receiver);

		MPI_Recv(&currVote, 1, MPI_INT, sender, 0, MPI_COMM_WORLD, MPI_STATUSES_IGNORE);
		printf("Process %d received vote %d from process %d\n", rank, currVote, sender);
		if (currVote > myVote) {
			leader = currVote;
			MPI_Send(&myVote, 1, MPI_INT, receiver, 0, MPI_COMM_WORLD);
			printf("Process %d sent vote %d  to process %d\n", rank, currVote, receiver);
		}
	}

	MPI_Barrier(MPI_COMM_WORLD);

	MPI_Finalize();

	if (rank == 0)
		printf("Leader is %d", leader);

	return 0;
}