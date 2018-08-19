package com.tu.thesis.helpers;

public class SQLConstants {

	// SQL Prepared statements for Flow object
	public static final String GET_ALL_FLOWS = "select * from Flow;";
	public static final String INSERT_NEW_FLOW = "insert into Flow(num_groups) values (?);";
	
	
}
