package main.java.controllers;

import annotation.Controller;
import annotation.UrlMapping;

@Controller
public class BackController {
    @UrlMapping(url = "/alert/test")
    public void bonjour(){

    }

    @UrlMapping(url = "/alert/list")
    public void wesh(String s){

    }

    @UrlMapping(url = "/alert/list", method = "POST")
    public void test(){

    }

    @UrlMapping(url = "/alert/test")
    public void transfert(){

    }
}
