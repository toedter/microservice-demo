package com.toedter.msd.userservice.user.ui;

import com.toedter.msd.userservice.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

	private final UserRepository userRepository;

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping("/list.html")
	public ModelAndView ItemList() {
		return new ModelAndView("userList", "users", userRepository.findAll());
	}
}
