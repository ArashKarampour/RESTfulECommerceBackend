package com.SpringBootRESTAPIs.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(Model model){
        // we can add attributes to the model and they will be available in the template
        model.addAttribute("name", "Arash");

        return "index"; // for templates just the name of the file without extension, spring will automatically look for it in the templates folder
    }
}
