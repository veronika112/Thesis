package com.tu.thesis.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tu.thesis.entity.Semesters;

public class SQLSemestersHelper {

	static Connection conn = null;

	private void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/TU", "postgres", "123");
	}

	public SQLSemestersHelper() {
		try {
			this.init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Semesters> retrieveAllSemesters(String getAllSemesters) throws SQLException, ClassNotFoundException {

		Statement stmt = null;
		List<Semesters> retrievedSemesters = new ArrayList<>();
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(getAllSemesters);

		while (rs.next()) {
			int id = rs.getInt("id");
			int numGroups = rs.getInt("year");
			String name = rs.getString("name");

			retrievedSemesters.add(new Semesters(id, numGroups, name));
		}
		return retrievedSemesters;
	}

	public List<Semesters> retrieveAllSemestersByYear(String getAllSemestersByYear, int year)
			throws SQLException, ClassNotFoundException {

		List<Semesters> retrievedSemesters = new ArrayList<>();

		PreparedStatement preparedStatement = conn.prepareStatement(getAllSemestersByYear);
		preparedStatement.setInt(1, year);

		ResultSet rs = preparedStatement.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");

			retrievedSemesters.add(new Semesters(id, year, name));
		}
		return retrievedSemesters;
	}
}
