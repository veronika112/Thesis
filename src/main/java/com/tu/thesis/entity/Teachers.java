package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Teachers {

	@Id
	private String id;
	private String name;

	// unavailable_days days, #??????????????????????????
	// unavailable_time VARCHAR(255), #??????????????????

	private String leadsLecture;
	private String leadsExercises;

	public Teachers() {
	}

	public Teachers(String id, String name, String leadsLecture, String leadsExercises) {
		super();
		this.id = id;
		this.name = name;
		this.leadsLecture = leadsLecture;
		this.leadsExercises = leadsExercises;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeadsLecture() {
		return leadsLecture;
	}

	public void setLeadsLecture(String leadsLecture) {
		this.leadsLecture = leadsLecture;
	}

	public String getLeadsExercises() {
		return leadsExercises;
	}

	public void setLeadsExercises(String leadsExercises) {
		this.leadsExercises = leadsExercises;
	}

}
