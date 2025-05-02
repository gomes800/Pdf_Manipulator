package com.gom.pdf.manipulator.services;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfService {

    private final String uploadDir;

    @Autowired
    public PdfService(com.gom.pdf.manipulator.config.StorageProperties storageProperties) {
        this.uploadDir = storageProperties.getLocation();
    }

    public void imageToPdf(MultipartFile file) throws IOException {
        String tempPath = uploadDir + "/" + file.getOriginalFilename();
        File tempFile = new File(tempPath);
        file.transferTo(tempFile);

        try(PDDocument document = new PDDocument()) {
            BufferedImage bufferedImage = ImageIO.read(tempFile);
            PDImageXObject img = LosslessFactory.createFromImage(document, bufferedImage);
            PDPage page = new PDPage();
            document.addPage(page);

            PDRectangle mediabox = page.getMediaBox();
            float pageWidth = mediabox.getWidth();
            float pageHeight = mediabox.getHeight();

            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();

            float widthScale = pageWidth / imageWidth;
            float heightScale = pageHeight / imageHeight;
            float scale = Math.min(widthScale, heightScale);

            float scaledWidth = imageWidth * scale;
            float scaledHeight = imageHeight * scale;

            float x = (pageWidth - scaledWidth) / 2;
            float y = (pageHeight - scaledHeight) / 2;

            try (PDPageContentStream contentStream = new
                    PDPageContentStream(document, page)) {
                contentStream.drawImage(img, x, y, scaledWidth, scaledHeight);
            }
            document.save(uploadDir + "/" + file.getOriginalFilename() + ".pdf");
        } finally {
            tempFile.delete();
        }
    }

    public void splitPdf(MultipartFile file, int fromPage, int toPage) throws IOException{
        String tempPath = uploadDir + "/" + file.getOriginalFilename();
        File tempFile = new File(tempPath);
        file.transferTo(tempFile);

        try (PDDocument document = PDDocument.load(tempFile)){
            PDDocument splitDocument = new PDDocument();
            int start = fromPage - 1;
            int end = toPage - 1;

            for (int i =start; i <= end; i++) {
                splitDocument.addPage(document.getPage(i));
            }
            splitDocument.save(uploadDir + "/" + "splitDoc" + ".pdf");
            splitDocument.close();
        } finally {
            tempFile.delete();
        }
    }

    //PDFMergerUtility PDFMergerUtility PDFMergerUtility PDFMergerUtility
    public void mergePdf(String pdfPath1, String pdfPath2, String outputPath) throws IOException {
    }

}

