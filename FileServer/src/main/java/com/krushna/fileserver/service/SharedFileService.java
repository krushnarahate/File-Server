package com.krushna.fileserver.service;

import com.krushna.fileserver.dto.ShareFileRequestDto;
import com.krushna.fileserver.dto.SharedFileResponseDto;
import com.krushna.fileserver.entity.Activity;
import com.krushna.fileserver.entity.FileData;
import com.krushna.fileserver.entity.SharedFile;
import com.krushna.fileserver.entity.User;
import com.krushna.fileserver.repository.ActivityRepository;
import com.krushna.fileserver.repository.FileRepository;
import com.krushna.fileserver.repository.SharedFileRepository;
import com.krushna.fileserver.repository.UserRepository;
import com.krushna.fileserver.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedFileService {

    private final SharedFileRepository sharedFileRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final AuthUtil authUtil;

    public String shareFile(Long fileId, ShareFileRequestDto dto) {

        User owner = authUtil.loggedInUser(userRepository);

        FileData file = fileRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found"));

        if (file.getUser().getUserId() != owner.getUserId()) {
            throw new RuntimeException("You are not the owner of this file");
        }

        User receiver = userRepository.findByUsername(dto.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (receiver.getUserId() == owner.getUserId()) {
            throw new RuntimeException("You cannot share file with yourself");
        }

        if (sharedFileRepository.findByFileAndSharedWith(file, receiver).isPresent()) {
            throw new RuntimeException("File already shared with this user");
        }

        SharedFile sharedFile = SharedFile.builder()
            .file(file)
            .owner(owner)
            .sharedWith(receiver)
            .sharedTime(LocalDateTime.now())
            .build();

        sharedFileRepository.save(sharedFile);

        Activity activity = new Activity();
        activity.setUsername(owner.getUsername());
        activity.setAction("SHARE");
        activity.setFileName(file.getFileName());
        activity.setTime(LocalDateTime.now());

        activityRepository.save(activity);

        return "File Shared Successfully";
    }

    public List<SharedFileResponseDto> getSharedFiles() {

        User user = authUtil.loggedInUser(userRepository);

        List<SharedFile> sharedFiles =
        sharedFileRepository.findBySharedWith(user);

        return sharedFiles.stream()
            .map(shared -> new SharedFileResponseDto(
                    shared.getFile().getId(),
                    shared.getFile().getFileName(),
                    shared.getOwner().getUsername()
            ))
        .toList();
    }

    public String removeSharedFile(Long shareId) {

        User owner = authUtil.loggedInUser(userRepository);

        SharedFile sharedFile = sharedFileRepository.findById(shareId)
            .orElseThrow(() -> new RuntimeException("Shared File Not Found"));

        if (sharedFile.getOwner().getUserId() != owner.getUserId()) {
            throw new RuntimeException("Access Denied");
        }

        sharedFileRepository.delete(sharedFile);

        return "Sharing Removed Successfully";
    }

    public FileData downloadSharedFile(Long fileId) {

        User user = authUtil.loggedInUser(userRepository);

        FileData file = fileRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File Not Found"));

        if (file.getUser().getUserId() == user.getUserId()) {
            return file;
        }

        boolean shared = sharedFileRepository
                .findByFileAndSharedWith(file, user)
            .isPresent();

        if (!shared) {
            throw new RuntimeException("Access Denied");
        }

        Activity activity = new Activity();
        activity.setUsername(user.getUsername());
        activity.setAction("DOWNLOAD");
        activity.setFileName(file.getFileName());
        activity.setTime(LocalDateTime.now());

        activityRepository.save(activity);

        return file;
    }
}