package com.iseem_backend.application.utils;

import com.iseem_backend.application.model.Diplome;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;

public class DiplomePDFGenerator {

    public static byte[] generatePDF(Diplome diplome) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Diplôme: " + diplome.getNomDiplome())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18));

            document.add(new Paragraph("Type: " + (diplome.getTypeDiplome() != null ? diplome.getTypeDiplome().name() : "N/A")));
            document.add(new Paragraph("Année d'obtention: " + (diplome.getAnneeObtention() != null ? diplome.getAnneeObtention() : "N/A")));
            document.add(new Paragraph("Mention: " + (diplome.getMention() != null ? diplome.getMention().name() : "N/A")));
            document.add(new Paragraph("Validé: " + (diplome.isEstValide() ? "Oui" : "Non")));
            if (diplome.getSignatureAdmin() != null) {
                document.add(new Paragraph("Signé par: " + diplome.getSignatureAdmin().getNom() + " " + diplome.getSignatureAdmin().getPrenom()));
            }
            if (diplome.getQrCodeUrl() != null) {
                document.add(new Paragraph("QR Code URL: " + diplome.getQrCodeUrl()));
            }

            document.add(new Paragraph("Commentaires: " + (diplome.getCommentaire() != null ? diplome.getCommentaire() : "")));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF du diplôme", e);
        }
    }
}
