package com.inno;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;



public class Main {
	public static final String  Company = "106";
	public static final String Bucket = "Pierce";
	
	public static void serializeToFile(String filename, List<String> list) {
		try{
	         FileOutputStream fos= new FileOutputStream(filename);
	         ObjectOutputStream oos= new ObjectOutputStream(fos);
	         oos.writeObject(list);
	         oos.close();
	         fos.close();
	       }catch(IOException ioe){
	            ioe.printStackTrace();
	        }
	}

	@SuppressWarnings("unchecked")
	public static  List<String>  deserializeFromFile(String filename) {
		 List<String> list = new ArrayList<String>();
		try{
	         FileInputStream fos= new FileInputStream(filename);
	         ObjectInputStream oos= new ObjectInputStream(fos);
	         list = (ArrayList<String>) oos.readObject();
	         oos.close();
	         fos.close();
	       }catch(IOException | ClassNotFoundException ioe){
	            ioe.printStackTrace();
	        }
		return list;
	}
	
	
	/**
	 * This method fetches profiles from MongoDb and inserts them into Cassandra, where company, bucket and profileid are used as row keys
	 * and profile itself as value
	 * @param args
	 */
	 public static void main(String[] args) {
	    	String collectionName = "SampleProfiles";
	    	int num = 208;
	    	
	    	if (args.length == 2) {
	    		collectionName = args[0];
	    		num = Integer.parseInt(args[1]);
	    	}
	    	System.out.println ("MongoDB " + collectionName + " size: " + num);
	    	
	        MongoClient mongoClient;
	   try{     
		   
	      //  mongoClient = new MongoClient( "ec2-54-228-63-91.eu-west-1.compute.amazonaws.com" , 27017 );
		   mongoClient = new MongoClient( "ec2-54-228-63-91.eu-west-1.compute.amazonaws.com" , 27017 );
			DB db = mongoClient.getDB( "shahab" );
			DBCollection coll = db.getCollection(collectionName);
			DBCursor cursor = coll.find();
			long t_start = 0;
			int counter = 0;
			try {
				BlockingQueue<DBObject> queue = new ArrayBlockingQueue<DBObject> (10000);
				ExecutorService executor = Executors.newFixedThreadPool(4);
				
			    List<Future<String>> list = new ArrayList<Future<String>>();
			    for (int i = 0; i < 4; i++) {
			      Callable<String> worker = new WriterThread(queue);
			      Future<String> submit = executor.submit(worker);
			      list.add(submit);
			    }
			    System.out.println("Thread have started .... ");
			     t_start = System.currentTimeMillis();
			    while(cursor.hasNext() ) {
				      DBObject data = cursor.next();
				      queue.offer(data, 10, TimeUnit.SECONDS);
				      counter++;
				      if (counter %  1000 == 0)
				      System.out.println ("Reading from Mongo : "  + counter );
			    }
			    
			    System.out.println("----------------");
			    // now retrieve the result
			    int i = 0;
			    for (Future<String> future : list) {
			      try {
			    	i++; 
			        System.out.println(i + ": " + future.get());
			      } catch (InterruptedException e) {
			        e.printStackTrace();
			      } catch (ExecutionException e) {
			        e.printStackTrace();
			      }
			    }
			    long t_end = System.currentTimeMillis();
				   System.out.println("Reading is finished, it took  "+ (t_end - t_start)+ "  ms " + counter + ". Doing serialization.... " ); 
			    executor.shutdown();
			
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
				   cursor.close();
				}
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
	        
	    }
	
	
	
  
}
