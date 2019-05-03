package com.rohit.springbootrestoauth2security.restController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "Hello Home Rest......";
	}

	@PreAuthorize("oauth2.hasScope('read')")
	@RequestMapping("/publishes")
	public String publicPage() {
		return "Public Page";
	}

	@PreAuthorize("#oauth2.hasScope('read')")
	@GetMapping("/private")
	public String privatePage() {
		return "Private Page";
	}

	@PreAuthorize("#oauth2.hasScope('read')")
	@GetMapping("/admin")
	public String admin() {
		return "Administrator Page";
	}
}
