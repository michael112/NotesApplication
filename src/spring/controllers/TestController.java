package spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hibernate.mappedclasses.User;
import java.util.*;

@Controller
@RequestMapping("/")
public class TestController {

    @RequestMapping("index.html")
    public ModelAndView getTestMessage() {
        ModelAndView returnModel = new ModelAndView("test");

        List<User> users = showAllUsers();

        returnModel.addObject("users", users);

        return returnModel;
    }

    private Integer insertUser(String login, String passwordSalt, String passwordHash, String eMail, String name, String surname) {
        try {
            hibernate.controllers.UsersController usersController = new hibernate.controllers.UsersController();
            return new Integer(usersController.addUser(new User(login, passwordSalt, passwordHash, eMail, name, surname)));
        }
        catch( ExceptionInInitializerError ex ) {
            return null;
        }
    }

    private List<User> showAllUsers() {
        try {
            hibernate.controllers.UsersController usersController = new hibernate.controllers.UsersController();
            return usersController.readAllUsers();
        }
        catch( ExceptionInInitializerError ex ) {
            return null;
        }
    }

}
