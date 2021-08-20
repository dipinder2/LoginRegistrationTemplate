package com.dipinder.loginregistration.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dipinder.loginregistration.models.User;
import com.dipinder.loginregistration.services.UserService;
import com.dipinder.loginregistration.validator.UserValidator;

// imports removed for brevity
@Controller
public class UserController {
	@Autowired
    private UserService userService;
	@Autowired
	private UserValidator userValidator;
	
	
	
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        
        return "user/registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "user/loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	userValidator.validate(user, result);
    	if(!result.hasErrors()) {
            userService.registerUser(user);
            return "redirect:/login";
        } else {
            return "user/registrationPage.jsp";
        }
    }
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session, RedirectAttributes flash) {
        // if the user is authenticated, save their user id in session
        if(userService.authenticateUser(email, password)) {
        	User user = userService.findByEmail(email);
            session.setAttribute("user", user.getId());
            return "redirect:/home";
        } else {
            flash.addFlashAttribute("error", "Invalid Email or Password!");
            return "redirect:/login";
        }
        // else, add error messages and return the login page
    }
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	if(session.getAttribute("user") == null) {
    		return "redirect:/login";
    	}
    	model.addAttribute(userService.findUserById((Long)session.getAttribute("uuid")));
        return "user/homePage.jsp";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
        // invalidate session
        // redirect to login page
    }
}



