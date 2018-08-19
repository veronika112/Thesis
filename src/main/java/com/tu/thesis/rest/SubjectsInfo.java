package com.tu.thesis.rest;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.SubjectsImpl;
import com.tu.thesis.entity.Subjects;

@RestController
public class SubjectsInfo {

	@RequestMapping(value = "/subjects/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Subjects> subjects() throws ClassNotFoundException, SQLException {

		return SubjectsImpl.retrieveAllSubjects();
	}

	@RequestMapping(value = "/subjects/semester", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Subjects> subjectsBySemesters(@RequestParam("id") int id)
			throws ClassNotFoundException, SQLException {

		return SubjectsImpl.retrieveAllSubjectsBySemester(id);
	}

	@RequestMapping(value = "/subjects/add", method = RequestMethod.POST, produces = "application/json")
	public void addSubjects(@RequestBody Subjects subjects) throws ClassNotFoundException, SQLException {

		SubjectsImpl.insertSubjects(subjects);
	}

	
	@RequestMapping(value = "/subjects/delete", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void deleteGWById(@RequestParam("id") int id) throws ClassNotFoundException, SQLException {

		SubjectsImpl.deleteSubjects(id);
	}

}
