#include <iostream>
#include <cuda.h>
#include <cstdlib>
#include <stdio.h>
#include <string>
#include <assert.h>
#include <cuda_runtime.h>
#include <cuda_runtime_api.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;
// GPU constant memory to hold our kernels (extremely fast access time)
__constant__ float convolutionKernelStore[256];

/**
* Cuda convolution function.  Destination will have the same width/height as source
*
* @param source      Source image host
* @param width       Source image width
* @param height      Source image height
* @param paddingX    source image padding x
* @param paddingY    source image padding y
* @param kOffset     offset of kernel data storage in memory
* @param kWidth      kernel width
* @param kHeight     kernel height
* @param destination destination image
*/
__global__ void convolve(unsigned char *source, int width, int height, int paddingX, int paddingY, unsigned int kOffset, int kWidth, int kHeight, unsigned char *destination)
{
	// Calculate our pixel's location
	int x = (blockIdx.x * blockDim.x) + threadIdx.x;
	int y = (blockIdx.y * blockDim.y) + threadIdx.y;

	float sum = 0.0;
	int   pWidth = kWidth / 2;
	int   pHeight = kHeight / 2;

	//Only valid pixels are executed
	if (x >= pWidth + paddingX &&   y >= pHeight + paddingY &&  x < (blockDim.x * gridDim.x) - pWidth - paddingX &&
		y < (blockDim.y * gridDim.y) - pHeight - paddingY)
	{
		for (int j = -pHeight; j <= pHeight; j++)
		{
			for (int i = -pWidth; i <= pWidth; i++)
			{
				// Sample the weight for this location
				int ki = (i + pWidth);
				int kj = (j + pHeight);
				float w = convolutionKernelStore[(kj * kWidth) + ki + kOffset];


				sum += w * float(source[((y + j) * width) + (x + i)]);
			}
		}
	}

	// Medium sum
	destination[(y * width) + x] = (unsigned char)sum;
}

__global__ void pythagoras(unsigned char *a, unsigned char *b, unsigned char *c)
{
	int idx = (blockIdx.x * blockDim.x) + threadIdx.x;

	float af = float(a[idx]);
	float bf = float(b[idx]);

	c[idx] = (unsigned char)sqrtf(af*af + bf*bf);
}

// create image buffer
unsigned char* createImageBuffer(unsigned int bytes, unsigned char **devicePtr)
{
	unsigned char *ptr = NULL;
	cudaSetDeviceFlags(cudaDeviceMapHost);
	cudaHostAlloc(&ptr, bytes, cudaHostAllocMapped);
	cudaHostGetDevicePointer(devicePtr, ptr, 0);
	return ptr;
}


int main(int argc, char** argv) {
	// open webcam
	cv::VideoCapture camera(0);
	cv::Mat          frame;
	if (!camera.isOpened())
		return -1;

	// capture windows
	//cv::namedWindow("Source");
	//cv::namedWindow("Greyscale");
	//cv::namedWindow("Blurred");
	cv::namedWindow("Sobel");

	// Functions for execution time 
	cudaEvent_t start, stop;
	cudaEventCreate(&start);
	cudaEventCreate(&stop);

	// Create gaussian  kernel(sum = 159)
	const float gaussianKernel5x5[25] =
	{
		2.f / 159.f,  4.f / 159.f,  5.f / 159.f,  4.f / 159.f, 2.f / 159.f,
		4.f / 159.f,  9.f / 159.f, 12.f / 159.f,  9.f / 159.f, 4.f / 159.f,
		5.f / 159.f, 12.f / 159.f, 15.f / 159.f, 12.f / 159.f, 5.f / 159.f,
		4.f / 159.f,  9.f / 159.f, 12.f / 159.f,  9.f / 159.f, 4.f / 159.f,
		2.f / 159.f,  4.f / 159.f,  5.f / 159.f,  4.f / 159.f, 2.f / 159.f,
	};
	cudaMemcpyToSymbol(convolutionKernelStore, gaussianKernel5x5, sizeof(gaussianKernel5x5), 0);
	const unsigned int gaussianKernel5x5Offset = 0;

	// Sobel gradient kernels
	const float sobelGradientX[9] =
	{
		-1.f, 0.f, 1.f,
		-2.f, 0.f, 2.f,
		-1.f, 0.f, 1.f,
	};
	const float sobelGradientY[9] =
	{
		1.f, 2.f, 1.f,
		0.f, 0.f, 0.f,
		-1.f, -2.f, -1.f,
	};
	cudaMemcpyToSymbol(convolutionKernelStore, sobelGradientX, sizeof(sobelGradientX), sizeof(gaussianKernel5x5));
	cudaMemcpyToSymbol(convolutionKernelStore, sobelGradientY, sizeof(sobelGradientY), sizeof(gaussianKernel5x5) + sizeof(sobelGradientX));
	const unsigned int sobelGradientXOffset = sizeof(gaussianKernel5x5) / sizeof(float);
	const unsigned int sobelGradientYOffset = sizeof(sobelGradientX) / sizeof(float) + sobelGradientXOffset;

	// Create CPU/GPU shared images
	camera >> frame;
	unsigned char *sourceDataDevice, *blurredDataDevice, *edgesDataDevice;
	cv::Mat source(frame.size(), CV_8U, createImageBuffer(frame.size().width * frame.size().height, &sourceDataDevice));
	cv::Mat blurred(frame.size(), CV_8U, createImageBuffer(frame.size().width * frame.size().height, &blurredDataDevice));
	cv::Mat edges(frame.size(), CV_8U, createImageBuffer(frame.size().width * frame.size().height, &edgesDataDevice));

	// Create 2 temporary images(sobel gradients)
	unsigned char *deviceGradientX, *deviceGradientY;
	cudaMalloc(&deviceGradientX, frame.size().width * frame.size().height);
	cudaMalloc(&deviceGradientY, frame.size().width * frame.size().height);

	//capture images
	while (1)
	{
		// grey scale picture
		camera >> frame;
		cvtColor(frame, source, CV_BGR2GRAY);

		// record start time
		cudaEventRecord(start);
		{
			// convolution kernel  parameters
			dim3 cblocks(frame.size().width / 16, frame.size().height / 16);
			dim3 cthreads(16, 16);

			// pythagoran kernel parameters
			dim3 pblocks(frame.size().width * frame.size().height / 256);
			dim3 pthreads(256, 1);

			//  gaussian blur (first kernel in store at 0)
			convolve << <cblocks, cthreads >> >(sourceDataDevice, frame.size().width, frame.size().height, 0, 0, gaussianKernel5x5Offset, 5, 5, blurredDataDevice);

			// sobel gradient convolutions (x&y padding is now 2 because there is a border of 2 around a 5x5 gaussian filtered image)
			convolve << <cblocks, cthreads >> >(blurredDataDevice, frame.size().width, frame.size().height, 2, 2, sobelGradientXOffset, 3, 3, deviceGradientX);
			convolve << <cblocks, cthreads >> >(blurredDataDevice, frame.size().width, frame.size().height, 2, 2, sobelGradientYOffset, 3, 3, deviceGradientY);
			pythagoras << <pblocks, pthreads >> >(deviceGradientX, deviceGradientY, edgesDataDevice);

			cudaThreadSynchronize();
		}
		cudaEventRecord(stop);

		// show execution time
		float ms = 0.0f;
		cudaEventSynchronize(stop);
		cudaEventElapsedTime(&ms, start, stop);
		std::cout << "Elapsed GPU time: " << ms << " milliseconds" << std::endl;

		// Muestra resultados
		//imshow("Source", frame);
		//imshow("Greyscale", source);
		//imshow("Blurred", blurred);
		imshow("Sobel", edges);

		// Spin
		if (cv::waitKey(1) == 27) break;
	}

	// Exit
	cudaFreeHost(source.data);
	cudaFreeHost(blurred.data);
	cudaFreeHost(edges.data);
	cudaFree(deviceGradientX);
	cudaFree(deviceGradientY);

	return 0;
}