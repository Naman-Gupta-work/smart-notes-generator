package org.smart_notes_generator.controller;

import org.smart_notes_generator.Response.FileDownloadResponse;
import org.smart_notes_generator.service.FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
public class FileUploadController {

    @Autowired
    FileDownloadService fileDownloadService;

    @PostMapping("/upload")
    public ResponseEntity<FileDownloadResponse> uploadFile(@RequestParam("file") MultipartFile file,String action) {
        FileDownloadResponse fileDownloadResponse=new FileDownloadResponse();
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            fileDownloadResponse.setStatus("FAILURE");
            fileDownloadResponse.setMessage("File name is Missing");
            return new ResponseEntity<>(fileDownloadResponse, HttpStatus.BAD_REQUEST);
        }
        String fileName = StringUtils.cleanPath(originalFilename);
        if (fileName.contains("..")) {
            fileDownloadResponse.setStatus("FAILURE");
            fileDownloadResponse.setMessage("Filename contains an invalid path sequence: " + fileName);
            return new ResponseEntity<>(fileDownloadResponse, HttpStatus.BAD_REQUEST);
        }
        fileDownloadResponse=fileDownloadService.uploadFile(file,action);
        if (fileDownloadResponse.getMessage().equals("FAILURE")){
            return new ResponseEntity<>(fileDownloadResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(fileDownloadResponse,HttpStatus.OK);

    }

}
