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
        return ImageIO.read(new File(path));
    }

    public void imageToPdf(String imagePath, String fileName, String destDir) throws IOException {
        try(PDDocument document = new PDDocument()) {
            BufferedImage bufferedImage = readImage(imagePath);
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
            document.save(destDir + "/" + fileName + ".pdf");
        }
    }

}

