package com.inno;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

public class CassClient {

	private Cluster cluster;
	private Session session;
	
	public void connect(String node, String keyspace) {
		   cluster = Cluster.builder()
		         .addContactPoint(node)
		         .build();
		   Metadata metadata = cluster.getMetadata();
		   System.out.printf("Connected to cluster: %s\n", 
		         metadata.getClusterName());
		   for ( Host host : metadata.getAllHosts() ) {
		      System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
		         host.getDatacenter(), host.getAddress(), host.getRack());
		   }
		   
		   session = cluster.connect(keyspace);
	}
	
	public void execute(String sqlStm) {
		session.execute(sqlStm);
	}
	
	public void close() {
		   cluster.close();
	}
}
