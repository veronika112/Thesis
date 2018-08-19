package com.tu.thesis.rest;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.TeachersImpl;
import com.tu.thesis.entity.Teachers;

@RestController
public class TeachersInfo {

	@RequestMapping(value = "/teachers/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Teachers> teachers() throws ClassNotFoundException, SQLException {

		return TeachersImpl.retrieveAllTeachers();
	}

	@RequestMapping(value = "/teachers/bySubject", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Teachers> teachersBySubject(@RequestParam("id") int id,
			@RequestParam("type") boolean type) throws ClassNotFoundException, SQLException {

		return TeachersImpl.retrieveAllTeachersBySubject(id, type);
	}

	@RequestMapping(value = "/teachers/add", method = RequestMethod.POST, produces = "application/json")
	public void addTeachers(@RequestBody Teachers teachers) throws ClassNotFoundException, SQLException {

		TeachersImpl.insertTeachers(teachers);
	}

	@RequestMapping(value = "/teachers/delete", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void delete(@RequestParam("id") int id) throws ClassNotFoundException, SQLException {

		TeachersImpl.deleteTeachers(id);
	}

}
