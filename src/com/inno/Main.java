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
import java.util.Random;



public class Main {
	public static final int  Company = 82;
	public static final int Bucket = 206;
	
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
	
	
	
	
    public static void main(String[] args) {
    	String collectionName = "SampleProfiles";
    	int num = 208;
    	
    	if (args.length == 2) {
    		collectionName = args[0];
    		num = Integer.parseInt(args[1]);
    		System.out.println ("MongoDB " + collectionName + " size: " + num);
    	}
    	
        MongoClient mongoClient;
		try {
			String filename = "data/pids_one_milion_nilson.txt";
			                                
			mongoClient = new MongoClient( "ec2-54-228-63-91.eu-west-1.compute.amazonaws.com" , 27017 );
			DB db = mongoClient.getDB( "shahab" );
			
//			//initialize cassandra client
//			CassClient client = new CassClient();
//			//127.0.0.1
//			client.connect("ec2-54-247-46-227.eu-west-1.compute.amazonaws.com", "mydb");
//			System.out.println("Connected to Cassandra !");
			
			ProfileDAO daoProfile = new ProfileDAO("ec2-54-73-244-254.eu-west-1.compute.amazonaws.com", "mydb");
		//	ProfileDAO daoProfile = new ProfileDAO("localhost", "mydb");
			
			DBCollection coll = db.getCollection(collectionName);
			DBCursor cursor = coll.find();
			List<String> keysList = new ArrayList<String> ();
			
			List<Integer> listInsert = new ArrayList<Integer>();
			List<Integer> listRead = new ArrayList<Integer>();
			
			long tStartInsert, tEndInsert;
			long tStartRead, tEndRead;
			int r;
			
			Random rand = new Random(System.currentTimeMillis());
			try {
			   int counter = 0;
			   long t_start = System.currentTimeMillis();
			   while(cursor.hasNext()) {
			      DBObject data = cursor.next();
			      data.removeField("_id");

			      String id = data.get("id").toString();
			     

			      tStartInsert = System.currentTimeMillis();
			      daoProfile.insertProfile(Company, Bucket, id, data.toString());
			      tEndInsert = System.currentTimeMillis() - tStartInsert;
			      listInsert.add(Integer.valueOf((int) tEndInsert));
			      
			      keysList.add(id);
			      r = rand.nextInt(keysList.size());
			      String read_id = keysList.get(r);
			      
			      tStartRead = System.currentTimeMillis();
			      daoProfile.readProfile(Company, Bucket, read_id);
			      tEndRead = System.currentTimeMillis() - tStartRead;
			      listRead.add(Integer.valueOf((int) tEndRead));
			      
//			      counter ++;
//			      if (counter % 100 == 0)
//			    	  System.out.println("Read " + counter);
//			      if (counter == num)
//			    	  break;
			      
			   }
			   long t_end = System.currentTimeMillis();
			   System.out.println("Reading is finished, it took  "+ (t_end - t_start)+ "  ms " + counter + ". Doing serialization.... " );
//			   serializeToFile(filename, keysList);
//			   System.out.println(" done !");
//			   List<String> readList = deserializeFromFile(filename);
//			   System.out.println("Desrialized list size = " + readList.size());
			   
			   System.out.println ("Read/Insert operations = " + listInsert.size());
			   
			   MyStat statInsert = new MyStat(listInsert);
			   System.out.println("*** Insert Mean = " + statInsert.getMean() + ", sd= " + statInsert.getStdDev());
			   
			   MyStat statRead = new MyStat(listRead);
			   System.out.println("*** Read Mean = " + statRead.getMean() + ", sd= " + statRead.getStdDev());
			   
			} finally {
			 //  client.close();
				daoProfile.close();
			   cursor.close();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
    }
}
