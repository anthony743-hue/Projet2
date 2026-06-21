package main.java.controllers;

import annotation.Controller;
import annotation.UrlMapping;

@Controller
public class FrontController {
    
    @UrlMapping(url = "/front/index")
    public void getHome(){

    }
}
