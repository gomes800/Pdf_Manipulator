package com.gom.pdf.manipulator.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtils {

    public static File saveWithUniqueName(MultipartFile file, String uploadDir) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        String uniqueFileName = uniqueId + file.getOriginalFilename();
        String tempPath = uploadDir + "/" + uniqueFileName;
        File tempFile = new File(tempPath);
        file.transferTo(tempFile);
        return tempFile;
    }
}
