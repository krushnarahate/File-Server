package com.krushna.fileserver.repository;

import com.krushna.fileserver.entity.FileData;
import com.krushna.fileserver.entity.SharedFile;
import com.krushna.fileserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedFileRepository extends JpaRepository<SharedFile,Long> {

    List<SharedFile> findBySharedWith(User user);
    Optional<SharedFile> findByFileAndSharedWith(FileData file, User sharedWith);
    Optional<SharedFile> findByFileAndSharedWithAndOwner(
            FileData file,
            User sharedWith,
            User owner
    );

}
