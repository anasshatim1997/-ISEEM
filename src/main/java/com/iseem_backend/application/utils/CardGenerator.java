package com.iseem_backend.application.utils;

import com.iseem_backend.application.model.Student;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CardGenerator {

    public static byte[] generateCards(List<Student> students) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            for (Student s : students) {
                Paragraph p = new Paragraph("Carte Scolaire", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

                final PdfPTable table = getPdfPTable(s);
                document.add(table);

                document.newPage();
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate student cards", ex);
        }
    }

    private static PdfPTable getPdfPTable(Student s) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Matricule:");
        table.addCell(s.getMatricule());
        table.addCell("Nom:");
        table.addCell(s.getUser().getNom());
        table.addCell("Prenom:");
        table.addCell(s.getUser().getPrenom());
        table.addCell("Date de naissance:");
        table.addCell(s.getDateNaissance() != null ? s.getDateNaissance().toString() : "");
        table.addCell("Sexe:");
        table.addCell(s.getSexe() != null ? s.getSexe().name() : "");
        table.addCell("Niveau:");
        table.addCell(s.getNiveau() != null ? s.getNiveau().name() : "");
        table.addCell("Email:");
        table.addCell(s.getUser().getEmail());
        table.addCell("Telephone:");
        table.addCell(s.getUser().getTelephone());
        return table;
    }
}
