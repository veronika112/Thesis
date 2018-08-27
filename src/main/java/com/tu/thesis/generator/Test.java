package com.tu.thesis.generator;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		// TODO Auto-generated method stub
		GeneratorImpl gi = new GeneratorImpl();
		gi.computeSchedule(3);	
		gi.printRes();
	}

}
