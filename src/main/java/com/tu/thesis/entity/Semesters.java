package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Semesters {

	@Id
	private int id;
	private String name;

	public Semesters() {
	}

	public Semesters(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
