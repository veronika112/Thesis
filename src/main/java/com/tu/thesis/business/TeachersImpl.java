package com.tu.thesis.business;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tu.thesis.entity.Teachers;
import com.tu.thesis.helpers.SQLConstants;
import com.tu.thesis.helpers.SQLTeachersHelper;

public class TeachersImpl {

	@Autowired
	public static SQLTeachersHelper sql = new SQLTeachersHelper();

	public static List<Teachers> retrieveAllTeachers() throws ClassNotFoundException, SQLException {
		return sql.retrieveAllTeachers(SQLConstants.GET_ALL_TEACHERS);
	}

	public static List<Teachers> retrieveAllTeachersBySubject(int id, boolean type)
			throws ClassNotFoundException, SQLException {
		return sql.retrieveAllTeachersBySubjectAndLectureType(SQLConstants.GET_ALL_TEACHERS_BY_SUBJECT, id, type);
	}

	public static void insertTeachers(Teachers teachers) throws ClassNotFoundException, SQLException {
		sql.insertTeacher(SQLConstants.INSERT_TEACHER, teachers);
	}

	public static void deleteTeachers(int id) throws ClassNotFoundException, SQLException {
		sql.deleteTeacher(SQLConstants.DELETE_TEACHER_BY_ID, id);
	}

}
