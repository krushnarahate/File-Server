package com.krushna.fileserver.repository;


import com.krushna.fileserver.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findByUsername(String username);

}
