package com.iseem_backend.application.utils;

import com.iseem_backend.application.model.EmploiDuTemps;
import com.iseem_backend.application.model.EmploiSlot;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;

public class EmploiDuTempsGenerator {

    public static byte[] generatePDF(EmploiDuTemps emploi) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Emploi du Temps: " + emploi.getId())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18));

            if (emploi.getSlots() != null) {
                for (EmploiSlot slot : emploi.getSlots()) {
                    document.add(new Paragraph(
                            slot.getJour() + " - " +
                                    slot.getHeureDebut() + " à " +
                                    slot.getHeureFin() + " : " +
                                    slot.getModule()
                    ));
                }
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
