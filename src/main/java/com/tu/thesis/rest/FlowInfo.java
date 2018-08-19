package com.tu.thesis.rest;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.FlowImpl;
import com.tu.thesis.entity.Flow;

@RestController
public class FlowInfo {

	@RequestMapping(value = "/flows/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Flow> flows() throws ClassNotFoundException, SQLException {

		return FlowImpl.retrieveAllFlows();
	}
}
