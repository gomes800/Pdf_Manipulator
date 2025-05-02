package com.gom.pdf.manipulator.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void mustCreatePdfFromImage() throws Exception {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test-image.png", "image/png", imageBytes);

        PdfService service = new PdfService(tempDir.toString());

        service.imageToPdf(mockFile);

        File expectedPdf = tempDir.resolve("test-image.png.pdf").toFile();
        assertTrue(expectedPdf.exists(), "Pdf not created.");
    }

    private byte[] createSinglePagePdf() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    @Test
    public void mustMergePdfs() throws Exception {
        byte[] pdf1 = createSinglePagePdf();
        byte[] pdf2 = createSinglePagePdf();

        MockMultipartFile mockFile1 = new MockMultipartFile(
                "file1", "file1.pdf", "application/pdf", pdf1);
        MockMultipartFile mockFile2 = new MockMultipartFile(
                "file2", "file2.pdf", "application/pdf", pdf2);

        PdfService service = new PdfService(tempDir.toString());

        service.mergePdf(mockFile1, mockFile2);

        File mergedFile = Files.list(tempDir)
                .filter(path -> path.getFileName().toString().endsWith("-merged.pdf"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Arquivo merged não encontrado!"))
                .toFile();

        assertTrue(mergedFile.exists(), "O PDF mesclado não foi criado!");

        try (PDDocument doc = PDDocument.load(mergedFile)) {
            assertEquals(2, doc.getNumberOfPages());
        }
    }

    private byte[] createMultiPagePdf(int numPages) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < numPages; i++) {
                doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    @Test
    public void mustSplitPdf() throws Exception {
        byte[] pdfBytes = createMultiPagePdf(5);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", pdfBytes);

        PdfService service = new PdfService(tempDir.toString());

        service.splitPdf(mockFile, 2, 4);

        File expectedPdf = tempDir.resolve("splitDoc.pdf").toFile();
        assertTrue(expectedPdf.exists(), "O PDF separado não foi criado!");

        try (PDDocument doc = PDDocument.load(expectedPdf)) {
            assertEquals(3, doc.getNumberOfPages());
        }
    }
}
