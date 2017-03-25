package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws IOException{
		ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> b = new ArrayList<ArrayList<Integer>>();
		ArrayDeque<Integer> sharedQueue = new ArrayDeque<>();
		
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
		
		for(int i = 0; i < a.get(0).size() ; i++){
			sharedQueue.add(i);
		}
		
		// omp parallel ThreadNum(4)
		{
			while (!sharedQueue.isEmpty()) {
				
				// omp critical
				int x = sharedQueue.removeFirst();
				
				for(int i = 0; i < b.get(0).size(); i++){
					for(int j = 0; j < a.get(0).size(); j++){
						c.get(x).set(i, c.get(x).get(i) + a.get(x).get(j) * b.get(j).get(i));						
					}
			}
			
			}
		}
		
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
