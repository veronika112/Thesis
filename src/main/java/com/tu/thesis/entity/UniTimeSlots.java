package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UniTimeSlots {

	@Id
	private int id;
	private String name;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public String toString() {
		return "{ \"id\":" + id + ", \"name\":\"" + name + "\"}";
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniTimeSlots other = (UniTimeSlots) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public UniTimeSlots(int currTimeSlot) {
		super();
		this.id = currTimeSlot;
		this.name="";
	}

	public UniTimeSlots(int id, String name) {
		super();
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
