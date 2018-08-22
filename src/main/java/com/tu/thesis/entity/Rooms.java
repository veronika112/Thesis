package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Rooms {

	@Id
	private int id;
	private boolean isLecture;

	public Rooms() {
	}

	@Override
	public String toString() {
		return "Rooms [getId()=" + getId() + ", isLecture()=" + isLecture() + ", hashCode()=" + hashCode() + "]";
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (isLecture ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rooms other = (Rooms) obj;
		if (id != other.id)
			return false;
		if (isLecture != other.isLecture)
			return false;
		return true;
	}

}
