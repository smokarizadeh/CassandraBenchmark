package com.inno;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;

public class ProfileDAO extends CassandraData {
	
	String host;
	String keyspace;
	BoundStatement boundInsertStatement;
	BoundStatement boundReadStatement;
	
	public ProfileDAO (String host, String keyspace) {
		this.host = host;
		this.keyspace = keyspace;
		prepareInsertStatement();
		prepareReadStatement();
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
		boundInsertStatement = new  BoundStatement(statement);
	}
	
	public void prepareReadStatement() {
		String host_tbl = keyspace + "." + "profiles_1";
		PreparedStatement statement = getSession(host, keyspace).prepare("select * from " + host_tbl +" where company= ? and  bucket= ? and id = ? " );
			   
		boundReadStatement = new  BoundStatement(statement);
	}
	
	public void insertProfile(int cid, int bid, String pid, String content) {
//		String sqlStm = makeInsertStatement (cid, bid, pid, content);
//		getSession(host, keyspace).execute(sqlStm);
		getSession(host, keyspace).execute(boundInsertStatement.bind (cid, bid, pid, content ));
	}
	
	
	public void readProfile(int cid, int bid, String pid) {
		getSession(host, keyspace).execute(boundReadStatement.bind (cid, bid, pid ));
	} 
	public void close() {
		close(host, keyspace);
	}
}
