package com.gom.pdf.manipulator.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service

public class PdfService {

    public BufferedImage readImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\CAIXA1\\Documents\\Projects\\pdf-manipulator\\src\\images.jpg"));
        return image;
    }

    public void imageToPdf(String imagePath, String fileName, String destDir) throws IOException {
        try(PDDocument document = new PDDocument();
            InputStream in = new FileInputStream(imagePath)) {
            PDImageXObject img = LosslessFactory.createFromImage(document, readImage(imagePath));
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new
                    PDPageContentStream(document, page)) {
                contentStream.drawImage(img, 0, 0);
            }
            document.save(destDir + "/" + fileName + ".pdf");
        }
    }

}

