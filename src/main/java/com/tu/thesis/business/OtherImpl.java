package com.tu.thesis.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tu.thesis.entity.UniTimeSlots;
import com.tu.thesis.helpers.SQLConstants;
import com.tu.thesis.helpers.SQLOtherHelper;

public class OtherImpl {

	@Autowired
	public static SQLOtherHelper sql = new SQLOtherHelper();

	public static List<UniTimeSlots> retrieveAllTimeSlots() {

		List<UniTimeSlots> timeSlots = new ArrayList<>();
		try {
			timeSlots = sql.retrieveAllTimeSlots(SQLConstants.GET_ALL_TS);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return timeSlots;
	}

}
