package com.toedter.msd.userservice.user.ui;

import com.toedter.msd.userservice.user.User;
import com.toedter.msd.userservice.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/users.html", method = RequestMethod.GET)
    public ModelAndView userList(@ModelAttribute("user") User user) {
        return new ModelAndView("users", "users", userRepository.findAll());
    }

    @RequestMapping(value = "/users.html", method = RequestMethod.POST)
    public String post(@ModelAttribute("user") User user) {
        userRepository.save(user);
        return "redirect:/users.html";
    }

    @RequestMapping(value = "/users/{id}.html", method = RequestMethod.DELETE)
    public String delete(@ModelAttribute("user") User user) {
        if(user.getId() != null) {
            userRepository.delete(user.getId());
        }
        return "redirect:/users.html";
    }
}
