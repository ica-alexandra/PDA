package main;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunMe {

	public static void main(String args[]) throws InterruptedException, ExecutionException, IOException {
		
		int rows = 0;
		int threads = 4;
		
		ArrayList<ArrayList<Integer>> matrA = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> matrB = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> matrProd = new ArrayList<ArrayList<Integer>>();
		
		
		ArrayList<Task> collection = new ArrayList<Task>( );
		
		Scanner input = null;
		input = new Scanner(new File("file1.txt"));			
		while(input.hasNextLine()) {
			rows++;
		    Scanner scannerA = new Scanner(input.nextLine());
		    ArrayList<Integer> elements = new ArrayList<Integer>();
		    while(scannerA.hasNextInt()) {
		        elements.add(scannerA.nextInt());	    
		    }
		    matrA.add(elements);
		}

		
		input = new Scanner(new File("file2.txt"));		
		while(input.hasNextLine()) {
		    Scanner scannerB = new Scanner(input.nextLine());
		    ArrayList<Integer> elements = new ArrayList<Integer>();
		    while(scannerB.hasNextInt()) {
		        elements.add(scannerB.nextInt());	    
		    }
		    matrB.add(elements);
		}
		
		
		for(int i = 0; i < rows; i++){
			ArrayList<Integer> el = new ArrayList<Integer>();
			for(int j = 0; j < matrA.get(0).size(); j++){
				el.add(0);
			}
			 matrProd.add(el);
		}
		
		
		for(int i = 0; i < rows ; i++){
			Task task = new Task(i, matrA, matrB, matrProd);
			collection.add(task);
		}
		
				
		for(int i=0; i < collection.size();i++)
		{
			collection.get(i).run();
		}
		
		Writer wr  = new FileWriter("file3.txt");
		for(ArrayList item: matrProd){
			for(Object item2: item){
				wr.write(item2.toString() + " ");
			}
			wr.write(" ");
		}
		System.out.print("Done!");
		wr.close();
	}
}

