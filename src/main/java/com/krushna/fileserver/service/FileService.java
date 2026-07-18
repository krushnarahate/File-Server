package com.krushna.fileserver.service;

import com.krushna.fileserver.entity.Activity;
import com.krushna.fileserver.entity.FileData;
import com.krushna.fileserver.entity.User;
import com.krushna.fileserver.repository.ActivityRepository;
import com.krushna.fileserver.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final ActivityRepository activityRepository;

    private final String UPLOAD_DIR = "uploads/";

    public FileData uploadFile(MultipartFile file, User user) throws IOException {

        String fileName =
                UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(UPLOAD_DIR, fileName);

        Files.copy(file.getInputStream(), path);

        FileData fileData = new FileData();

        fileData.setFileName(file.getOriginalFilename());

        fileData.setFilePath(path.toString());

        fileData.setFileType(file.getContentType());

        fileData.setFileSize(file.getSize());

        fileData.setUser(user);

        fileData.setUploadTime(LocalDateTime.now());

        FileData savedFile = fileRepository.save(fileData);

        Activity activity = new Activity();

        activity.setUsername(user.getUsername());

        activity.setAction("UPLOAD");

        activity.setFileName(savedFile.getFileName());

        activity.setTime(LocalDateTime.now());

        activityRepository.save(activity);

        return savedFile;
    }

    public List<FileData> getAllFiles(User user) {

        return fileRepository.findByUser(user);
    }


    public FileData getFileById(Long id) {

        return fileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("File not found"));
    }

    public String deleteFile(
            Long id,
            String username
    ) throws IOException {

        FileData fileData = fileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("File not found"));

        if(!fileData.getUser().getUsername().equals(username)) {

            throw new RuntimeException(
                    "You are not allowed to delete this file"
            );
        }

        Path path = Paths.get(fileData.getFilePath());

        Files.deleteIfExists(path);

        fileRepository.delete(fileData);

        Activity activity = new Activity();

        activity.setUsername(username);

        activity.setAction("DELETE");

        activity.setFileName(fileData.getFileName());

        activity.setTime(LocalDateTime.now());

        activityRepository.save(activity);

        return "File deleted successfully";
    }
}