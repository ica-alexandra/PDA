package main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Fir extends Thread{

	private ArrayDeque<Integer> sharedQueue;
	ArrayList<ArrayList<Integer>> a;
	ArrayList<ArrayList<Integer>> b;
	ArrayList<ArrayList<Integer>> c;
	int x = 0;
	int id;

	public Fir(ArrayDeque<Integer> sharedQueue, ArrayList<ArrayList<Integer>> a, ArrayList<ArrayList<Integer>> b, ArrayList<ArrayList<Integer>> c, int i) {
        this.sharedQueue = sharedQueue;
        this.a = a;
		this.b = b;
		this.c = c;
		id = i;
    }
	
	
	@Override
    public void run() {
       
			while (!sharedQueue.isEmpty()) {
				
				synchronized(sharedQueue){
					x = sharedQueue.removeFirst();
				}
				
				//System.out.println(id);
				
				for(int i = 0; i < b.get(0).size(); i++){
					for(int j = 0; j < a.get(0).size(); j++){
						c.get(x).set(i, c.get(x).get(i) + a.get(x).get(j) * b.get(j).get(i));						
					}
				}

			}
	}
 	
}

