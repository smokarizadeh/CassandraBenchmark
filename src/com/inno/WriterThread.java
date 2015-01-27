package com.inno;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.mongodb.DBObject;

public class WriterThread implements Callable<String> {

	BlockingQueue<DBObject> myqueue;
	
	public WriterThread(BlockingQueue<DBObject> bq) {
		this.myqueue = bq;
	}
	 
	@Override
	public String call() throws Exception {
		long tStartInsert, tEndInsert;
		long tStartRead, tEndRead;
	
		//ProfileDAO daoProfile = new ProfileDAO("ec2-54-74-113-83.eu-west-1.compute.amazonaws.com", "mydb");
		//ProfileDAO daoProfile = new ProfileDAO("localhost", "mydb");
	
		String DatawareHousingNew1= "ec2-54-220-128-75.eu-west-1.compute.amazonaws.com";
		ProfileDAO daoProfile = new ProfileDAO(DatawareHousingNew1, "mydb");
		
		List<Integer> listInsert = new ArrayList<Integer>();
		List<Integer> listRead = new ArrayList<Integer>();
		List<Integer> listSize = new ArrayList<Integer> ();
		
		while (true){
			DBObject data = this.myqueue.poll(30, TimeUnit.SECONDS);
			  
			  if (data == null) {
				   MyStat statSize = new MyStat(listSize);
				   MyStat statInsert = new MyStat(listInsert);
				   String s1 = "*** Insert Mean = " + statInsert.getMean() + ", sd= " + statInsert.getStdDev() + 
						   " : " + listInsert.size() + " , Mean of profile size = " + statSize.getMean() + 
						   " , SD of profiel size: " +statSize.getStdDev();
				
				   MyStat statRead = new MyStat(listRead);
				   String s2 = "*** Read Mean = " + statRead.getMean() + ", sd= " + statRead.getStdDev() + 
						   ": " + listRead.size();
				
				   return s1 +"\n" + s2;
			  }
			  data.removeField("_id");
		      String id = data.get("id").toString();
		    
		     // c = Main.Company;
		      tStartInsert = System.currentTimeMillis();
		      daoProfile.insertProfile(Main.Company, Main.Bucket, id, data.toString());
		      tEndInsert = System.currentTimeMillis() - tStartInsert;
		}
	}

}
