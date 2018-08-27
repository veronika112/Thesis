package com.tu.thesis.generator;

import java.io.IOException;
import java.sql.SQLException;

public class Test {

	public static void main(String[] args) throws IOException, CloneNotSupportedException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		GeneratorImpl gi = new GeneratorImpl();
		gi.computeSchedule(3);	
		gi.printRes();
	}

}
