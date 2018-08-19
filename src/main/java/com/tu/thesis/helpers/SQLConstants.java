package com.tu.thesis.helpers;

public class SQLConstants {

	// SQL Prepared statements for Semesters object
	public static final String GET_ALL_SEMESTERS = "select * from Semesters;";
	public static final String GET_ALL_SEMESTERS_BY_YEAR = "select * from Semesters where year = ?;";

	// SQL Prepared statements for Subjects object
	public static final String GET_ALL_SUBJECTS = "select * from Subjects;";
	public static final String GET_ALL_SUBJECTS_BY_SEMESTERS = "select * from Subjects where sem_id = ?;";
	public static final String DELETE_SUBJECT_BY_ID = "delete from Subjects where id = ?";
	public static final String INSERT_SUBJECT = "insert into Subjects(sem_id, name, lectures_num, exercises_num) VALUES (?, ?, ?, ?);";

	// SQL Prepared statements for Teachers object
	public static final String GET_ALL_TEACHERS = "select * from Teachers;";
	public static final String GET_ALL_TEACHERS_BY_SUBJECT = "select * from Teachers where id in (Select teachers_id from Teachers_Subjects where subjects_id = ? and isLecture = ?);";
	public static final String DELETE_TEACHER_BY_ID = "delete from Teachers where id = ?";
	public static final String INSERT_TEACHER = "insert into Teachers(name) VALUES (?);";

	// SQL Prepared statements for Teachers object
	public static final String GET_ALL_ROOMS = "select * from Rooms;";
	public static final String GET_ROOM_BY_ID = "select * from Rooms where id = ?;";
	public static final String DELETE_ROOM_BY_ID = "delete from Rooms where id = ?";
	public static final String INSERT_ROOM = "insert into Rooms(id, isLecture) VALUES (?, ?);";
}
