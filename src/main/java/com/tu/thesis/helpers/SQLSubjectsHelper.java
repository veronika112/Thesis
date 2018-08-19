package com.tu.thesis.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tu.thesis.entity.Subjects;

public class SQLSubjectsHelper {

	static Connection conn = null;

	private void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/TU", "postgres", "123");
	}

	public SQLSubjectsHelper() {
		try {
			this.init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Subjects> retrieveAllSubjects(String getAllSubjects) throws SQLException, ClassNotFoundException {

		Statement stmt = null;
		List<Subjects> retrievedSubjects = new ArrayList<>();
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(getAllSubjects);

		while (rs.next()) {
			int id = rs.getInt("id");
			int semId = rs.getInt("sem_id");
			String name = rs.getString("name");
			int lectureNum = rs.getInt("lectures_num");
			int exercisesNum = rs.getInt("exercises_num");

			retrievedSubjects.add(new Subjects(id, semId, name, lectureNum, exercisesNum));
		}
		return retrievedSubjects;
	}

	public List<Subjects> retrieveAllSubjectsBySemester(String getAllSubjectsBySemester, int semester)
			throws SQLException, ClassNotFoundException {

		List<Subjects> retrievedSubjects = new ArrayList<>();

		PreparedStatement preparedStatement = conn.prepareStatement(getAllSubjectsBySemester);
		preparedStatement.setInt(1, semester);

		ResultSet rs = preparedStatement.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			int semId = rs.getInt("sem_id");
			String name = rs.getString("name");
			int lectureNum = rs.getInt("lectures_num");
			int exercisesNum = rs.getInt("exercises_num");

			retrievedSubjects.add(new Subjects(id, semId, name, lectureNum, exercisesNum));
		}
		return retrievedSubjects;
	}

	public void deleteSubject(String deleteSubject, int id) throws SQLException, ClassNotFoundException {

		PreparedStatement preparedStatement = conn.prepareStatement(deleteSubject);
		preparedStatement.setInt(1, id);

		preparedStatement.executeUpdate();
	}

	public void insertSubject(String insertSubject, Subjects s) throws ClassNotFoundException, SQLException {

		PreparedStatement preparedStatement = conn.prepareStatement(insertSubject);
		preparedStatement.setInt(1, s.getSem_id());
		preparedStatement.setString(2, s.getName());
		preparedStatement.setInt(3, s.getLecture_num());
		preparedStatement.setInt(4, s.getExercises_num());

		preparedStatement.executeUpdate();

		return;
	}

}
