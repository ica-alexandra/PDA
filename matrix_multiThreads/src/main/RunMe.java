package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunMe {

	public static void main(String args[]) throws InterruptedException, ExecutionException, IOException {
				
		ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> b = new ArrayList<ArrayList<Integer>>();
			
		Scanner input = null;
		input = new Scanner(new File("file1.txt"));			
		while(input.hasNextLine()) {
		    Scanner rowReader = new Scanner(input.nextLine());
		    ArrayList row = new ArrayList();
		    while(rowReader.hasNextInt()) {
		        row.add(rowReader.nextInt());	
		    }
		    a.add(row);
		}
		
		input = new Scanner(new File("file2.txt"));		
		while(input.hasNextLine()) {
		    Scanner rowReader = new Scanner(input.nextLine());
		    ArrayList row = new ArrayList();
		    while(rowReader.hasNextInt()) {
		        row.add(rowReader.nextInt());		        
		    }
		    b.add(row);
		}
		
		ArrayList<ArrayList<Integer>> c = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < a.get(0).size(); i++){
			ArrayList row = new ArrayList();
			for(int j = 0; j < a.get(0).size(); j++){
				row.add(0);
			}
			 c.add(row);
		}
		
		
		ArrayDeque<Integer> sharedQueue = new ArrayDeque<Integer>();
		
		for(int i = 0; i < a.get(0).size() ; i++){
			sharedQueue.add(i);
		}
		
		
		Fir fir1 = new Fir(sharedQueue,a,b,c,1);
		Fir fir2 = new Fir(sharedQueue,a,b,c,2);
		Fir fir3 = new Fir(sharedQueue,a,b,c,3);
		Fir fir4 = new Fir(sharedQueue,a,b,c,4);
		
		fir1.start();
		fir2.start();
		fir3.start();
		fir4.start();
		
		fir1.join();
		fir2.join();
		fir3.join();
		fir4.join();
		
			
		Writer wr  = new FileWriter("file3.txt");
		for(ArrayList item: c){
			for(Object item2: item){
				wr.write(item2.toString() + " ");
			}
			wr.write(" ");
		}
		System.out.print("Done!");
	}
}

