package main.java.controllers;

import annotation.Controller;
import annotation.UrlMapping;

@Controller
public class FrontController {
    
    @UrlMapping(path = "/emp/test")
    public void test(){

    }

    @UrlMapping(path = "/emp/list")
    public void testMaster(String s){

    }
}
