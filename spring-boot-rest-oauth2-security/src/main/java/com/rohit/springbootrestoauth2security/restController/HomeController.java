package com.rohit.springbootrestoauth2security.restController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@PreAuthorize("#oauth2.hasScope('read')")
	@GetMapping("/home")
	public String home() {
		return "Hello Rest......";
	}
}
