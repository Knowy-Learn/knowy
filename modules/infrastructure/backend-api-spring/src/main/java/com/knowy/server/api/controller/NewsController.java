package com.knowy.server.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("news")
public class NewsController {

	@GetMapping
	public Map<String, Object> getNews() {
		return null;
	}
}