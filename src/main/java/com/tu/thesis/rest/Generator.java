package com.tu.thesis.rest;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.OtherImpl;
import com.tu.thesis.generator.GeneratorImpl;


@RestController
public class Generator {

	@RequestMapping(value = "/generate", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity generate(@RequestParam("groups") int groups, @RequestBody() String jsonStructure) throws ClassNotFoundException, SQLException, IOException, CloneNotSupportedException {

		new GeneratorImpl().computeSchedule(groups, jsonStructure);
		
		return new ResponseEntity(HttpStatus.OK);
	}
}

