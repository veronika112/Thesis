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

	@Override
	public String toString() {
		return "Subjects [getId()=" + getId() + ", getSem_id()=" + getSem_id() + ", getName()=" + getName()
				+ ", getLecture_num()=" + getLecture_num() + ", getExercises_num()=" + getExercises_num()
				+ ", hashCode()=" + hashCode() + "]";
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + exercises_num;
		result = prime * result + id;
		result = prime * result + lecture_num;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + sem_id;
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
		Subjects other = (Subjects) obj;
		if (exercises_num != other.exercises_num)
			return false;
		if (id != other.id)
			return false;
		if (lecture_num != other.lecture_num)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sem_id != other.sem_id)
			return false;
		return true;
	}

}
