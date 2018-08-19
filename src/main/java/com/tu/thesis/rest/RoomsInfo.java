package com.tu.thesis.rest;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tu.thesis.business.RoomsImpl;
import com.tu.thesis.entity.Rooms;

@RestController
public class RoomsInfo {

	@RequestMapping(value = "/rooms/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Rooms> rooms() throws ClassNotFoundException, SQLException {

		return RoomsImpl.retrieveAllRooms();
	}

	@RequestMapping(value = "/rooms/byId", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Rooms> roomsById(@RequestParam("id") int id) throws ClassNotFoundException, SQLException {

		return RoomsImpl.retrieveAllRoomsById(id);
	}

	@RequestMapping(value = "/rooms/add", method = RequestMethod.POST, produces = "application/json")
	public void addRooms(@RequestBody Rooms rooms) throws ClassNotFoundException, SQLException {

		RoomsImpl.insertRoom(rooms);
	}

	@RequestMapping(value = "/rooms/delete", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void delete(@RequestParam("id") int id) throws ClassNotFoundException, SQLException {

		RoomsImpl.deleteRoom(id);
	}

}
