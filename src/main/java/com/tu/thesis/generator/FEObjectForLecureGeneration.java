package com.tu.thesis.generator;

import java.util.Map;
import java.util.Set;

import com.tu.thesis.entity.Rooms;
import com.tu.thesis.entity.Subjects;
import com.tu.thesis.entity.Teachers;
import com.tu.thesis.entity.UniTimeSlots;

public class FEObjectForLecureGeneration {

	private Rooms room;
	private Subjects subject;
	private Teachers teacher;
	private Map<DAYS, Set<UniTimeSlots>> availableTime;

	public FEObjectForLecureGeneration(Rooms room, Subjects subject, Teachers teacher,
			Map<DAYS, Set<UniTimeSlots>> availableTime) {

		super();
		this.room = room;
		this.subject = subject;
		this.teacher = teacher;
		this.availableTime = availableTime;
	}

	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
		this.room = room;
	}

	public Subjects getSubject() {
		return subject;
	}

	public void setSubject(Subjects subject) {
		this.subject = subject;
	}

	public Teachers getTeacher() {
		return teacher;
	}

	public void setTeacher(Teachers teacher) {
		this.teacher = teacher;
	}

	public Map<DAYS, Set<UniTimeSlots>> getAvailableTime() {
		return availableTime;
	}

	public void setAvailableTime(Map<DAYS, Set<UniTimeSlots>> availableTime) {
		this.availableTime = availableTime;
	}

	@Override
	public String toString() {
		return "FEObjectForLecureGeneration [room=" + room + ", subject=" + subject + ", teacher=" + teacher
				+ ", availableTime=" + availableTime + "]\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableTime == null) ? 0 : availableTime.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		FEObjectForLecureGeneration other = (FEObjectForLecureGeneration) obj;
		if (availableTime == null) {
			if (other.availableTime != null)
				return false;
		} else if (!availableTime.equals(other.availableTime))
			return false;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			return false;
		return true;
	}

}
