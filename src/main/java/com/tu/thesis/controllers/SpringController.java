package com.tu.thesis.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpringController {

		@RequestMapping("/")
		public String home() {

			return "index.html";
		}
}

