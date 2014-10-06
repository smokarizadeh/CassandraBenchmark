package com.inno;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.mongodb.DBObject;

public class WriterThread implements Callable<String> {

	BlockingQueue<DBObject> myqueue;
	
	public static final int Bucket = 206;
	
	public WriterThread(BlockingQueue<DBObject> bq) {
		this.myqueue = bq;
	}
	 


	@Override
	public String call() throws Exception {
		long tStartInsert, tEndInsert;
		long tStartRead, tEndRead;
		int r;
		int c,b;
		
		Random rand1 = new Random (System.nanoTime());
		Random rand2 = new Random (System.nanoTime());
		
		ProfileDAO daoProfile = new ProfileDAO("ec2-54-73-244-254.eu-west-1.compute.amazonaws.com", "mydb");
		//ProfileDAO daoProfile = new ProfileDAO("localhost", "mydb");
		List<String> keysList = new ArrayList<String> ();
		
		List<Integer> listInsert = new ArrayList<Integer>();
		List<Integer> listRead = new ArrayList<Integer>();
		
		long size = 0;
		while (true){
			DBObject data = this.myqueue.poll(30, TimeUnit.SECONDS);
			  
			  if (data == null) {
				   MyStat statInsert = new MyStat(listInsert);
				   String s1 = "*** Insert Mean = " + statInsert.getMean() + ", sd= " + statInsert.getStdDev() + 
						   " : " + listInsert.size() + " , size of content = " + size;
				   
				 //  System.out.println(s1);
				   MyStat statRead = new MyStat(listRead);
				   String s2 = "*** Read Mean = " + statRead.getMean() + ", sd= " + statRead.getStdDev() + 
						   ": " + listRead.size();
				  // System.out.println(s2);
				   
				   return s1 +"\n" + s2;
			  }
			  data.removeField("_id");

		      String id = data.get("id").toString();
		    
		      c = rand1.nextInt(200);
		      
		      tStartInsert = System.currentTimeMillis();
		      daoProfile.insertProfile(c, Bucket, id, data.toString());
		      tEndInsert = System.currentTimeMillis() - tStartInsert;
		      
		      listInsert.add(Integer.valueOf((int) tEndInsert));
		      keysList.add(id);
		      r = rand2.nextInt(keysList.size());
		      String read_id = keysList.get(r);
		      
		      c = rand2.nextInt(200);
		      tStartRead = System.currentTimeMillis();
		      daoProfile.readProfile(c, Bucket, read_id);
		      tEndRead = System.currentTimeMillis() - tStartRead;
		      listRead.add(Integer.valueOf((int) tEndRead));
		      
		      size += data.toString().length();
		      data = null; 
		}
	}

}
