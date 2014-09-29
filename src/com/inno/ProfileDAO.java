package com.inno;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;

public class ProfileDAO extends CassandraData {
	
	String host;
	String keyspace;
	BoundStatement boundStatement;
	
	public ProfileDAO (String host, String keyspace) {
		this.host = host;
		this.keyspace = keyspace;
		prepareInsertStatement();
	}
	
	public  String makeInsertStatement(int company, int bucket, String id, String content) {
		String sqlStm = "INSERT INTO profiles_1 (company, bucket, id, content) VALUES (" ;
		sqlStm += company + "," + bucket + ",'" + id + "','" + content + "')";
		
		return sqlStm;
	}
	
	public void prepareInsertStatement() {
		String host_tbl = keyspace + "." + "profiles_1";
		PreparedStatement statement = getSession(host, keyspace).prepare(
			      "INSERT INTO " + host_tbl +
			      " (company, bucket, id, content) " +
			      "VALUES (?, ?, ?, ?);");
	    boundStatement = new  BoundStatement(statement);
	}
	public void insertProfile(int cid, int bid, String pid, String content) {
//		String sqlStm = makeInsertStatement (cid, bid, pid, content);
//		getSession(host, keyspace).execute(sqlStm);
		getSession(host, keyspace).execute(boundStatement.bind (cid, bid, pid, content ));
	}
	
	public void close() {
		close(host, keyspace);
	}
}
