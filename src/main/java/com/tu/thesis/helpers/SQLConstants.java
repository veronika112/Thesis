package com.tu.thesis.helpers;

public class SQLConstants {

	// SQL Prepared statements for Semesters object
	public static final String GET_ALL_SEMESTERS = "select * from Semesters;";
	public static final String GET_ALL_SEMESTERS_BY_YEAR = "select * from Semesters where year = ?;";
	
	// SQL Prepared statements for Subjects object
	public static final String GET_ALL_SUBJECTS= "select * from Subjects;";
	public static final String GET_ALL_SUBJECTS_BY_SEMESTERS = "select * from Subjects where sem_id = ?;";
	public static final String DELETE_SUBJECT_BY_ID = "delete from Subjects where id = ?";
	public static final String INSERT_SUBJECT = "insert into Subjects(sem_id, name, lectures_num, exercises_num) VALUES (?, ?, ?, ?);";
	
	
	
}
