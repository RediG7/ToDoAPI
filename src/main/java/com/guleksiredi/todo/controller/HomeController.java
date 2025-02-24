package com.guleksiredi.todo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    @GetMapping("/")
    public ModelAndView redirectToSwagger() {
        return new ModelAndView("redirect:" + swaggerPath);
    }
}