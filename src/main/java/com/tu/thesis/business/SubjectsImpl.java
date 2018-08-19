package com.tu.thesis.business;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tu.thesis.entity.Subjects;
import com.tu.thesis.helpers.SQLConstants;
import com.tu.thesis.helpers.SQLSubjectsHelper;

public class SubjectsImpl {

	@Autowired
	public static SQLSubjectsHelper sql = new SQLSubjectsHelper();

	public static List<Subjects> retrieveAllSubjects() throws ClassNotFoundException, SQLException {
		return sql.retrieveAllSubjects(SQLConstants.GET_ALL_SUBJECTS);
	}

	public static List<Subjects> retrieveAllSubjectsBySemester(int id) throws ClassNotFoundException, SQLException {
		return sql.retrieveAllSubjectsBySemester(SQLConstants.GET_ALL_SUBJECTS_BY_SEMESTERS, id);
	}

	public static void insertSubjects(Subjects subject) throws ClassNotFoundException, SQLException {
		sql.insertSubject(SQLConstants.INSERT_SUBJECT, subject);
	}

	public static void deleteSubjects(int id) throws ClassNotFoundException, SQLException {
		sql.deleteSubject(SQLConstants.DELETE_SUBJECT_BY_ID, id);
	}

}
