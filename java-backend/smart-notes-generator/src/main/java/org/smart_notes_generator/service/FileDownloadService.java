package org.smart_notes_generator.service;

import io.netty.util.internal.StringUtil;
import org.smart_notes_generator.Response.FileDownloadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileDownloadService {
    private final Path fileStorageLocation;

    public FileDownloadService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the upload directory.", ex);
        }
    }



    @Autowired
    RedisPublisher redisPublisher;

    public FileDownloadResponse uploadFile(MultipartFile file,String action) {
        FileDownloadResponse fileDownloadResponse=new FileDownloadResponse();
        String fileName= StringUtils.cleanPath(file.getOriginalFilename());
        String jobID= UUID.randomUUID().toString();
        String fileExtension="";
        int index=fileName.lastIndexOf(".");
        if(index>=0) {
            fileExtension = fileName.substring(index);
        }
        Path targetLocation = this.fileStorageLocation.resolve(jobID+fileExtension);
        try {

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String file_path = targetLocation.toAbsolutePath().toString();

            fileDownloadResponse.setMessage("File Uploaded Successfully");
            fileDownloadResponse.setStatus("SUCCESS");
            fileDownloadResponse.setJobId(jobID);
            redisPublisher.publish(jobID,file_path,action);
        } catch (IOException ex) {

            fileDownloadResponse.setMessage("Could Not store file");
            fileDownloadResponse.setStatus("FAILURE");
        }
        catch (Exception e){
            fileDownloadResponse.setMessage("Could not Publish Video");
            fileDownloadResponse.setStatus("FAILURE");
        }
        return fileDownloadResponse;
    }


}
