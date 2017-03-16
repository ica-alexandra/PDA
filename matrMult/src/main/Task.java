package main;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Task implements Runnable {
	
	int x ;
	ArrayList<ArrayList<Integer>> matrA;
	ArrayList<ArrayList<Integer>> matrB;
	ArrayList<ArrayList<Integer>> matrProd;
	
	
	public Task(int index, ArrayList<ArrayList<Integer>> a, ArrayList<ArrayList<Integer>> b, ArrayList<ArrayList<Integer>> r){
		this.x = index;
		this.matrA = a;
		this.matrB = b;
		this.matrProd = r;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < matrB.get(0).size(); i++){
			for(int j = 0; j < matrA.get(0).size(); j++){
				matrProd.get(x).set(i, matrProd.get(x).get(i) + matrA.get(x).get(j) * matrB.get(j).get(i));
			}
		}
	}
}
