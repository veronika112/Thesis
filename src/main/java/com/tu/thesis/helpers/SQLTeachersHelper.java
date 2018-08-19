package com.tu.thesis.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tu.thesis.entity.Teachers;

public class SQLTeachersHelper {

	static Connection conn = null;

	private void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/TU", "postgres", "123");
	}

	public SQLTeachersHelper() {
		try {
			this.init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Teachers> retrieveAllTeachers(String getAllTeachers) throws SQLException, ClassNotFoundException {

		Statement stmt = null;
		List<Teachers> retrievedTeachers = new ArrayList<>();
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(getAllTeachers);

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			
			retrievedTeachers.add(new Teachers(id, name));
		}
		return retrievedTeachers;
	}

	public List<Teachers> retrieveAllTeachersBySubjectAndLectureType(String getAllTeachersBySubjects, int subjectId,
			boolean lectureType) throws SQLException, ClassNotFoundException {

		List<Teachers> retrievedTeachers = new ArrayList<>();

		PreparedStatement preparedStatement = conn.prepareStatement(getAllTeachersBySubjects);
		preparedStatement.setInt(1, subjectId);
		preparedStatement.setBoolean(2, lectureType);

		ResultSet rs = preparedStatement.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");

			retrievedTeachers.add(new Teachers(id, name));
		}
		return retrievedTeachers;
	}

	public void deleteTeacher(String deleteTeacher, int id) throws SQLException, ClassNotFoundException {

		PreparedStatement preparedStatement = conn.prepareStatement(deleteTeacher);
		preparedStatement.setInt(1, id);

		preparedStatement.executeUpdate();
	}

	public void insertTeacher(String insertTeacher, Teachers s) throws ClassNotFoundException, SQLException {

		PreparedStatement preparedStatement = conn.prepareStatement(insertTeacher);
		preparedStatement.setString(1, s.getName());

		preparedStatement.executeUpdate();
		return;
	}

}
