package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Rooms {

	@Id
	private int id;
	private boolean isLecture;

	// unavailable_days days, #????????????????????????
	// unavailable_time VARCHAR(255) #?????????????????

	public Rooms() {
	}

	public Rooms(int id, boolean isLecture) {
		super();
		this.id = id;
		this.isLecture = isLecture;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isLecture() {
		return isLecture;
	}

	public void setLecture(boolean isLecture) {
		this.isLecture = isLecture;
	}

}
