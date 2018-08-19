package com.tu.thesis.rest;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.SemestersImpl;
import com.tu.thesis.entity.Semesters;

@RestController
public class SemestersInfo {

	@RequestMapping(value = "/semesters/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Semesters> semesters() throws ClassNotFoundException, SQLException {

		return SemestersImpl.retrieveAllSemesters();
	}
	
	@RequestMapping(value = "/semesters/year", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Semesters> semesters(@RequestParam("year") int year) throws ClassNotFoundException, SQLException {

		return SemestersImpl.retrieveAllSemestersByYear(year);
	}
}
