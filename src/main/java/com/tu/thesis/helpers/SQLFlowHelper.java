package com.tu.thesis.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tu.thesis.entity.Flow;

public class SQLFlowHelper {

	static Connection conn = null;

	private void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql:~/TU", "public", "");
	}

	public SQLFlowHelper() {
		try {
			this.init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertFlow(String insertFlow, Flow flow) throws SQLException {

		PreparedStatement preparedStatement = conn.prepareStatement(insertFlow);
		preparedStatement.setInt(1, flow.getNum_groups());

		preparedStatement.executeUpdate();

	}

	public List<Flow> retrieveAllFlows(String getAllFlows) throws SQLException, ClassNotFoundException {

		Statement stmt = null;
		List<Flow> retrievedFlows = new ArrayList<>();
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(getAllFlows);

		while (rs.next()) {
			int id = rs.getInt("id");
			int numGroups = rs.getInt("num_groups");
			retrievedFlows.add(new Flow(id, numGroups));
		}
		return retrievedFlows;
	}
}
