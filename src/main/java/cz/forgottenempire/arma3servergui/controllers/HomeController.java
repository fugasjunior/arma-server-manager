package cz.forgottenempire.arma3servergui.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// when the request URL is not found on backend side (eg. /dashboard), it gets redirected to /index.html where
// React Router takes over and displays the corresponding page (or custom 404 page)
//@Controller
public class HomeController{

//    @RequestMapping(value = "/**")
//    public String test() {
//        return "index.html";
//    }

//    private static final String PATH = "/error";
//
//    @RequestMapping(value = PATH)
//    public String error() {
//        return "forward:/index.html";
//    }
//
//    @Override
//    public String getErrorPath() {
//        return PATH;
//    }
}