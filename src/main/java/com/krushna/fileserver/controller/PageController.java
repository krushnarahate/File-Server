package com.krushna.fileserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup-page")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/dashboard-page")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/activities-page")
    public String activitiesPage() {
        return "activities";
    }

    @GetMapping("/admin-page")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/shared-files-page")
    public String sharedFilesPage() {
        return "shared-files";
    }
}
