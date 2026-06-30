package main.java.controllers;

import annotation.Controller;
import annotation.UrlMapping;
import helper.HttpMethod;

@Controller
public class BackController {
    @UrlMapping(url = "/alert/test")
    public void bonjour(){
        System.out.println("==== FANA ====");
    }

    @UrlMapping(url = "/alert/list")
    public void wesh(){
        System.out.println("==== COCO ====");
    }

    @UrlMapping(url = "/alert/list", method = HttpMethod.POST)
    public void test(){

    }
}
