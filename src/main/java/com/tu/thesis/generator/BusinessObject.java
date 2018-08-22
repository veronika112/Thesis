package com.tu.thesis.generator;

import com.tu.thesis.entity.Rooms;
import com.tu.thesis.entity.Subjects;
import com.tu.thesis.entity.Teachers;

public class BusinessObject {

	private Subjects sub;
	private Rooms room;
	private Teachers teacher;
	
	public BusinessObject() {}
	
	public BusinessObject(Subjects sub, Rooms room, Teachers teacher) {
		super();
		this.sub = sub;
		this.room = room;
		this.teacher = teacher;
	}
	public Subjects getSub() {
		return sub;
	}
	public void setSub(Subjects sub) {
		this.sub = sub;
	}
	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}
	public Teachers getTeacher() {
		return teacher;
	}
	public void setTeacher(Teachers teacher) {
		this.teacher = teacher;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + ((sub == null) ? 0 : sub.hashCode());
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
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
		BusinessObject other = (BusinessObject) obj;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (sub == null) {
			if (other.sub != null)
				return false;
		} else if (!sub.equals(other.sub))
			return false;
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BusinessObject [getSub()=" + getSub() + ", getRoom()=" + getRoom() + ", getTeacher()=" + getTeacher()
				+ "]";
	}

}
