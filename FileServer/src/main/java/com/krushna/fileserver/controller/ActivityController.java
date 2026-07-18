package com.krushna.fileserver.controller;

import com.krushna.fileserver.entity.Activity;
import com.krushna.fileserver.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityRepository activityRepository;

    @GetMapping("/my-activities")
    public List<Activity> getMyActivities() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return activityRepository
                .findByUsername(username);
    }
}