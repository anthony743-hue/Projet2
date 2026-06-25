package main.java.controllers;

import annotation.Controller;
import annotation.UrlMapping;

@Controller
public class BackController {
    @UrlMapping(path = "/alert/test")
    public void bonjour(){

    }

    @UrlMapping(path = "/alert/list")
    public void wesh(String s){

    }
}
