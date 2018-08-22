package com.tu.thesis.rest;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.OtherImpl;
import com.tu.thesis.entity.UniTimeSlots;

@RestController
public class OtherInfo {

	@RequestMapping(value = "/times/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<UniTimeSlots> times() throws ClassNotFoundException, SQLException {

		return OtherImpl.retrieveAllTimeSlots();
	}
}
