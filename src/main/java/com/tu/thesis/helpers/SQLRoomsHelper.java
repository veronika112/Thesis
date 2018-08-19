package com.tu.thesis.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tu.thesis.entity.Rooms;

public class SQLRoomsHelper {

	static Connection conn = null;

	private void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/TU", "postgres", "123");
	}

	public SQLRoomsHelper() {
		try {
			this.init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Rooms> retrieveAllRooms(String getAllRooms) throws SQLException, ClassNotFoundException {

		Statement stmt = null;
		List<Rooms> retrievedRooms = new ArrayList<>();
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(getAllRooms);

		while (rs.next()) {
			int id = rs.getInt("id");
			boolean isLecture = rs.getBoolean("isLecture");

			retrievedRooms.add(new Rooms(id, isLecture));
		}
		return retrievedRooms;
	}

	public List<Rooms> retrieveRoomById(String getRoomById, int id) throws SQLException, ClassNotFoundException {

		List<Rooms> retrievedTeachers = new ArrayList<>();

		PreparedStatement preparedStatement = conn.prepareStatement(getRoomById);
		preparedStatement.setInt(1, id);

		ResultSet rs = preparedStatement.executeQuery();

		while (rs.next()) {
			int rid = rs.getInt("id");
			boolean isLecture = rs.getBoolean("isLecture");

			retrievedTeachers.add(new Rooms(id, isLecture));
		}
		return retrievedTeachers;
	}

	public void deleteRoom(String deleteRoom, int id) throws SQLException, ClassNotFoundException {

		PreparedStatement preparedStatement = conn.prepareStatement(deleteRoom);
		preparedStatement.setInt(1, id);

		preparedStatement.executeUpdate();
	}

	public void insertRoom(String insertRoom, Rooms s) throws ClassNotFoundException, SQLException {

		PreparedStatement preparedStatement = conn.prepareStatement(insertRoom);
		preparedStatement.setInt(1, s.getId());
		preparedStatement.setBoolean(2, s.isLecture());

		preparedStatement.executeUpdate();
		return;
	}

}
