package com.tu.thesis.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Subjects {

	@Id
	private int id;
	private int sem_id;
	private String name;
	private int lecture_num;
	private int exercises_num;

	public Subjects() {
	}

	public Subjects(int id, int sem_id, String name, int lecture_num, int exercises_num) {
		super();
		this.id = id;
		this.sem_id = sem_id;
		this.name = name;
		this.lecture_num = lecture_num;
		this.exercises_num = exercises_num;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSem_id() {
		return sem_id;
	}

	public void setSem_id(int sem_id) {
		this.sem_id = sem_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLecture_num() {
		return lecture_num;
	}

	public void setLecture_num(int lecture_num) {
		this.lecture_num = lecture_num;
	}

	public int getExercises_num() {
		return exercises_num;
	}

	public void setExercises_num(int exercises_num) {
		this.exercises_num = exercises_num;
	}

}
