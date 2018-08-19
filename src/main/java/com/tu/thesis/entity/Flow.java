package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Flow {

	@Id
	private int id;
	private int num_groups;

	public Flow() {
	}

	public Flow(int id, int num_groups) {
		super();
		this.id = id;
		this.num_groups = num_groups;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum_groups() {
		return num_groups;
	}

	public void setNum_groups(int num_groups) {
		this.num_groups = num_groups;
	}

}
