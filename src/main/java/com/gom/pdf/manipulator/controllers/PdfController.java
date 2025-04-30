package com.gom.pdf.manipulator.controllers;

import com.gom.pdf.manipulator.config.StorageProperties;
import com.gom.pdf.manipulator.services.PdfService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class PdfController {

    private final PdfService pdfService;
    private final StorageProperties storageProperties;

    @Autowired
    public PdfController(PdfService pdfService, StorageProperties storageProperties) {
        this.pdfService = pdfService;
        this.storageProperties = storageProperties;
    }

    @PostMapping("/convert")
    public ResponseEntity<String> convertImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = storageProperties.getLocation();

            String tempPath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(tempPath));
            File savedFile = new File(tempPath);
            if (!savedFile.exists()) {
                throw new RuntimeException("Arquivo não foi salvo: " + tempPath);
            }

            pdfService.imageToPdf(tempPath, file.getOriginalFilename() + "-convertido", uploadDir);

            new File(tempPath).delete();

            return ResponseEntity.ok("Imagem convertida para PDF com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao converter: " + e.getMessage());
        }
    }

    @PostMapping("/split")
    public ResponseEntity<String> splitPdf(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = storageProperties.getLocation();

            String tempPath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(tempPath));
            File savedFile = new File(tempPath);
            if (!savedFile.exists()) {
                throw new RuntimeException("Arquivo não foi salvo: " + tempPath);
            }

            pdfService.splitPdf(tempPath, 2, 3, uploadDir);

            new File(tempPath).delete();

            return ResponseEntity.ok("PDF separado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao converter: " + e.getMessage());
        }
    }
}
