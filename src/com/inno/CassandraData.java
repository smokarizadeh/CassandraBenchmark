package com.inno;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;

public class CassandraData {

  //
  // A static variable that holds the session.  Only one of these will exist for the whole application
  //

  private static Session cassandraSession = null;

  /**
   * Required constructor, but it doesn't need to do anything.
   */

  CassandraData () {
    // Do nothing
  }

  /**
   *
   *  Return the Cassandra session.
   *  When the application starts up, the session
   *  is set to null.  When this function is called, it checks to see if the session is null.
   *  If so, it creates a new session, and sets the static session.
   *
   *  All of the DAO classes are subclasses of this
   *
   * @return - a valid cassandra session
   */

  public static Session getSession(String host, String keypsace) {

    if (cassandraSession == null) {
      cassandraSession = createSession(host, keypsace);
    }

    return cassandraSession;

  }

  /**
   *
   * Create a new cassandra Cluster() and Session().  Returns the Session.
   *
   * @return A new Cassandra session
   */

  protected static Session createSession(String host, String keyspace) {
    Cluster cluster = Cluster.builder().addContactPoint(host)
    		.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
    		.withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy()))
    		.build();
    
    
    Metadata metadata = cluster.getMetadata();
   System.out.printf("Connected to cluster: %s\n", 
         metadata.getClusterName());
   for ( Host h : metadata.getAllHosts() ) {
	      System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
	         h.getDatacenter(), h.getAddress(), h.getRack());
	   }
    return cluster.connect(keyspace);
  }
  
  
  protected void close(String host, String keyspace) {
	  if (cassandraSession != null) {
	      getSession(host, keyspace).getCluster().close();
	    }
  }
}
