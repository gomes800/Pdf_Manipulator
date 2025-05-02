package com.gom.pdf.manipulator.controllers;

import com.gom.pdf.manipulator.config.StorageProperties;
import com.gom.pdf.manipulator.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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
    public ResponseEntity<String> convertImage(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            pdfService.imageToPdf(file);
            return ResponseEntity.ok("Imagem convertida para PDF com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao converter: " + e.getMessage());
        }
    }

    @PostMapping("/split")
    public ResponseEntity<String> splitPdf(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            pdfService.splitPdf(file, 2, 3);
            return ResponseEntity.ok("PDF separado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao dividir: " + e.getMessage());
        }
    }
}
