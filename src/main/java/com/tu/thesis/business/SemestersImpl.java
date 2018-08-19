package com.tu.thesis.business;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tu.thesis.entity.Semesters;
import com.tu.thesis.helpers.SQLConstants;
import com.tu.thesis.helpers.SQLSemestersHelper;

public class SemestersImpl {

	@Autowired
	public static SQLSemestersHelper sql = new SQLSemestersHelper();

	public static List<Semesters> retrieveAllSemesters() throws ClassNotFoundException, SQLException {

		return sql.retrieveAllSemesters(SQLConstants.GET_ALL_SEMESTERS);

	}
	
	public static List<Semesters> retrieveAllSemestersByYear(int year) throws ClassNotFoundException, SQLException {

		return sql.retrieveAllSemestersByYear(SQLConstants.GET_ALL_SEMESTERS_BY_YEAR, year);

	}
}
