#include <iostream>
#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/opencv.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <opencv2/objdetect/objdetect.hpp>
#include <math.h>
#include <time.h>

using namespace cv;
using namespace std;

int main()
{
	// Reading image
	Mat img = imread("fff.png");

	clock_t tStart = clock();

	// Converting to grayscale
	Mat img_gray, image_blur;
	GaussianBlur(img, image_blur, Size(5, 5), 3, 3);
	cvtColor(image_blur, img_gray, CV_RGB2GRAY);


	// Displaying grayscale image
	//imshow("Original Image",img_gray);
	//waitKey(0);


	int cols = img_gray.cols;
	int rows = img_gray.rows;
	
	//Sobel edge detection function from OpenCV
	cv::Mat Gx, Gy; int ksize = 3;
	Mat abs_grad_x, abs_grad_y;
	cv::Sobel(img_gray, Gx, CV_8U, 1, 0, ksize);
	convertScaleAbs(Gx, abs_grad_x);
	cv::Sobel(img_gray, Gy, CV_8U, 0, 1, ksize);
	convertScaleAbs(Gy, abs_grad_y);
	Mat grad;
	addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);

	printf("Time taken: %.5fs\n", (double)(clock() - tStart) / CLOCKS_PER_SEC);

	imshow("Sobel Image", grad);
	waitKey(0);

	return 0;

} 