package com.krushna.fileserver.repository;

import com.krushna.fileserver.entity.FileData;
import com.krushna.fileserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileData,Long> {
    List<FileData> findByUser(User user);
}
