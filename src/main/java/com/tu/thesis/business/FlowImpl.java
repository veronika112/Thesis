package com.tu.thesis.business;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tu.thesis.entity.Flow;
import com.tu.thesis.helpers.SQLConstants;
import com.tu.thesis.helpers.SQLFlowHelper;

public class FlowImpl {

	@Autowired
	public static SQLFlowHelper sql = new SQLFlowHelper();

	public static List<Flow> retrieveAllFlows() throws ClassNotFoundException, SQLException {

		return sql.retrieveAllFlows(SQLConstants.GET_ALL_FLOWS);

	}
}
