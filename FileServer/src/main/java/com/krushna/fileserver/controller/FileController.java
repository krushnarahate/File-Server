package com.krushna.fileserver.controller;

import com.krushna.fileserver.entity.Activity;
import com.krushna.fileserver.entity.FileData;
import com.krushna.fileserver.entity.User;
import com.krushna.fileserver.repository.ActivityRepository;
import com.krushna.fileserver.repository.UserRepository;
import com.krushna.fileserver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public FileController(FileService fileService, UserRepository userRepository, ActivityRepository activityRepository) {
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @PostMapping("/upload")
    public FileData uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));


        return fileService.uploadFile(file, user);
    }

    @GetMapping("/my-files")
    public List<FileData> getFiles() throws Exception{

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsername(username).orElseThrow(() -> new  RuntimeException("User not found"));

        return fileService.getAllFiles(user);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id) throws IOException {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        FileData fileData = fileService.getFileById(id);

        Activity activity = new Activity();

        activity.setUsername(username);

        activity.setAction("DOWNLOAD");

        activity.setFileName(fileData.getFileName());

        activity.setTime(LocalDateTime.now());

        activityRepository.save(activity);

        Path path = Paths.get(fileData.getFilePath());

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        fileData.getFileType()
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                fileData.getFileName() + "\""
                )
                .body(resource);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id) throws IOException {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return fileService.deleteFile(id, username);
    }

    @GetMapping("/my-activities")
    public List<Activity> getActivities() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return activityRepository.findByUsername(username);
    }
}
