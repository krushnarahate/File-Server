package com.krushna.fileserver.controller;

import com.krushna.fileserver.dto.ShareFileRequestDto;
import com.krushna.fileserver.dto.SharedFileResponseDto;
import com.krushna.fileserver.entity.FileData;
import com.krushna.fileserver.service.SharedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shared-file")
public class SharedFileController {

    private final SharedFileService sharedFileService;

    @PostMapping("/share/{fileId}")
    public ResponseEntity<String> shareFile(
            @PathVariable Long fileId,
            @RequestBody ShareFileRequestDto requestDto) {

        return ResponseEntity.ok(
                sharedFileService.shareFile(fileId, requestDto)
        );
    }

    @GetMapping("/shared-files")
    public ResponseEntity<List<SharedFileResponseDto>> getSharedFiles() {

        return ResponseEntity.ok(
                sharedFileService.getSharedFiles()
        );
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<String> removeSharedFile(
            @PathVariable Long shareId) {

        return ResponseEntity.ok(
                sharedFileService.removeSharedFile(shareId)
        );
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadSharedFile(
            @PathVariable Long fileId)
            throws FileNotFoundException {

        FileData file =
                sharedFileService.downloadSharedFile(fileId);

        InputStreamResource resource =
                new InputStreamResource(
                        new FileInputStream(file.getFilePath())
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\""
                )
                .contentType(
                        MediaType.parseMediaType(file.getFileType())
                )
                .contentLength(file.getFileSize())
                .body(resource);
    }

}