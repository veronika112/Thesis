package com.tu.thesis.business;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tu.thesis.entity.Rooms;
import com.tu.thesis.helpers.SQLConstants;
import com.tu.thesis.helpers.SQLRoomsHelper;

public class RoomsImpl {

	@Autowired
	public static SQLRoomsHelper sql = new SQLRoomsHelper();

	public static List<Rooms> retrieveAllRooms() throws ClassNotFoundException, SQLException {
		return sql.retrieveAllRooms(SQLConstants.GET_ALL_ROOMS);
	}
	
	public static List<Rooms> retrieveAllRoomsByType(boolean type) throws ClassNotFoundException, SQLException {
		return sql.retrieveAllRoomsByType(SQLConstants.GET_ROOM_BY_TYPE, type);
	}
	
	public static List<Rooms> retrieveAllRoomsById(int id) throws ClassNotFoundException, SQLException {
		return sql.retrieveRoomById(SQLConstants.GET_ROOM_BY_ID, id);
	}
	
	public static void insertRoom(Rooms rooms) throws ClassNotFoundException, SQLException {
		sql.insertRoom(SQLConstants.INSERT_ROOM, rooms);
	}
	

	public static void deleteRoom(int id) throws ClassNotFoundException, SQLException {
		sql.deleteRoom(SQLConstants.DELETE_ROOM_BY_ID, id);
	}
	
}
