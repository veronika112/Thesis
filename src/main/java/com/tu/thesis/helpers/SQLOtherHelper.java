package com.tu.thesis.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tu.thesis.entity.UniTimeSlots;

public class SQLOtherHelper {

	static Connection conn = null;

	private void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/TU", "postgres", "123");
	}

	public SQLOtherHelper() {
		try {
			this.init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public List<UniTimeSlots> retrieveAllTimeSlots(String getAllTimeSlots) throws SQLException, ClassNotFoundException {

		Statement stmt = null;
		List<UniTimeSlots> retrievedTimeSlots = new ArrayList<>();
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(getAllTimeSlots);

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");

			retrievedTimeSlots.add(new UniTimeSlots(id, name));
		}
		return retrievedTimeSlots;
	}

}
