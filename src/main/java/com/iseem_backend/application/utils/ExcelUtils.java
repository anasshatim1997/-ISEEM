package com.iseem_backend.application.utils;

import com.iseem_backend.application.enums.Niveau;
import com.iseem_backend.application.enums.Role;
import com.iseem_backend.application.enums.Sex;
import com.iseem_backend.application.enums.Statut;
import com.iseem_backend.application.enums.YesOrNo;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.Formation;
import com.iseem_backend.application.model.Student;
import com.iseem_backend.application.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ExcelUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static byte[] exportDiplomes(List<Diplome> diplomes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Diplomes");
            String[] columns = {
                    "ID", "NomDiplome", "TypeDiplome", "AnneeObtention", "EstValide",
                    "Mention", "DateDelivrance", "SignatureAdmin", "QrCodeUrl", "Commentaire", "ModeRemise"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) header.createCell(i).setCellValue(columns[i]);
            int rowNum = 1;
            for (Diplome d : diplomes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(d.getIdDiplome().toString());
                row.createCell(1).setCellValue(d.getNomDiplome());
                row.createCell(2).setCellValue(d.getTypeDiplome() != null ? d.getTypeDiplome().name() : "");
                row.createCell(3).setCellValue(d.getAnneeObtention() != null ? d.getAnneeObtention() : 0);
                row.createCell(4).setCellValue(d.isEstValide());
                row.createCell(5).setCellValue(d.getMention() != null ? d.getMention().name() : "");
                row.createCell(6).setCellValue(d.getDateDelivrance() != null ? d.getDateDelivrance().toString() : "");
                row.createCell(7).setCellValue(d.getSignatureAdmin() != null ? d.getSignatureAdmin().getEmail() : "");
                row.createCell(8).setCellValue(d.getQrCodeUrl() != null ? d.getQrCodeUrl() : "");
                row.createCell(9).setCellValue(d.getCommentaire() != null ? d.getCommentaire() : "");
                row.createCell(10).setCellValue(d.getModeRemise() != null ? d.getModeRemise().name() : "");
            }
            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static List<Diplome> importDiplomes(MultipartFile file) throws IOException {
        List<Diplome> diplomes = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next();
            while (rows.hasNext()) {
                Row row = rows.next();
                Diplome d = new Diplome();
                d.setNomDiplome(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null);
                d.setTypeDiplome(row.getCell(2) != null ? Enum.valueOf(com.iseem_backend.application.enums.TypeDiplome.class, row.getCell(2).getStringCellValue()) : null);
                d.setAnneeObtention(row.getCell(3) != null ? (int) row.getCell(3).getNumericCellValue() : null);
                d.setEstValide(row.getCell(4) != null && row.getCell(4).getBooleanCellValue());
                d.setMention(row.getCell(5) != null ? Enum.valueOf(com.iseem_backend.application.enums.Mention.class, row.getCell(5).getStringCellValue()) : null);
                d.setDateDelivrance(row.getCell(6) != null && row.getCell(6).getCellType() == CellType.STRING
                        ? LocalDate.parse(row.getCell(6).getStringCellValue(), DATE_FORMATTER) : null);
                d.setQrCodeUrl(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : null);
                d.setCommentaire(row.getCell(9) != null ? row.getCell(9).getStringCellValue() : null);
                d.setModeRemise(row.getCell(10) != null ? Enum.valueOf(com.iseem_backend.application.enums.ModeRemise.class, row.getCell(10).getStringCellValue()) : null);
                diplomes.add(d);
            }
        }
        return diplomes;
    }

    public static byte[] exportFormations(List<Formation> formations) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Formations");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Durée", "Coût", "Professeurs", "Description", "Année", "ModeFormation", "NiveauAcces", "CapaciteMax", "EstActive"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }
            int rowNum = 1;
            for (Formation f : formations) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(f.getIdFormation().toString());
                row.createCell(1).setCellValue(f.getNom());
                row.createCell(2).setCellValue(f.getDuree());
                row.createCell(3).setCellValue(f.getCout() != null ? f.getCout().doubleValue() : 0);
                row.createCell(4).setCellValue(f.getNomProfesseurs());
                row.createCell(5).setCellValue(f.getDescription() != null ? f.getDescription() : "");
                row.createCell(6).setCellValue(f.getAnneeFormation() != null ? f.getAnneeFormation() : "");
                row.createCell(7).setCellValue(f.getModeFormation() != null ? f.getModeFormation().name() : "");
                row.createCell(8).setCellValue(f.getNiveauAcces() != null ? f.getNiveauAcces() : "");
                row.createCell(9).setCellValue(f.getCapaciteMax() != null ? f.getCapaciteMax() : 0);
                row.createCell(10).setCellValue(f.getEstActive() != null ? f.getEstActive() : false);
            }
            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        }
    }

    public static List<Formation> importFormations(MultipartFile file) throws IOException {
        List<Formation> formations = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next();
            while (rows.hasNext()) {
                Row row = rows.next();
                Formation f = new Formation();
                f.setNom(row.getCell(1).getStringCellValue());
                f.setDuree((int) row.getCell(2).getNumericCellValue());
                f.setCout(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
                f.setDescription(row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null);
                f.setAnneeFormation(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null);
                f.setModeFormation(Enum.valueOf(com.iseem_backend.application.enums.ModeFormation.class, row.getCell(7).getStringCellValue()));
                f.setNiveauAcces(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : null);
                f.setCapaciteMax((int) row.getCell(9).getNumericCellValue());
                f.setEstActive(row.getCell(10).getBooleanCellValue());
                formations.add(f);
            }
        }
        return formations;
    }

    public static List<Student> importStudents(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            log.info("Starting import - Total rows in sheet: {}", totalRows);

            Row headerRow = sheet.getRow(0);
            boolean hasPasswordColumn = false;
            if (headerRow != null && headerRow.getLastCellNum() >= 18) {
                String fifthColumnHeader = getCellValueAsString(headerRow.getCell(5));
                hasPasswordColumn = fifthColumnHeader != null &&
                        (fifthColumnHeader.equalsIgnoreCase("password") ||
                                fifthColumnHeader.equalsIgnoreCase("motDePasse") ||
                                fifthColumnHeader.equalsIgnoreCase("pwd"));
            }

            log.info("Header row exists: {}, Has password column: {}, Header columns: {}",
                    headerRow != null, hasPasswordColumn,
                    headerRow != null ? headerRow.getLastCellNum() : 0);

            if (headerRow != null) {
                log.info("Header content:");
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = headerRow.getCell(j);
                    log.info("Column {}: '{}'", j, getCellValueAsString(cell));
                }
            }

            int processedRows = 0;
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                log.info("Processing row {}: row exists = {}", i, row != null);

                if (isRowEmpty(row)) {
                    log.info("Row {} is empty, skipping", i);
                    continue;
                }

                processedRows++;
                log.info("Row {} data:", i);
                if (row != null) {
                    for (int j = 0; j < Math.min(18, row.getLastCellNum()); j++) {
                        Cell cell = row.getCell(j);
                        log.info("  Cell {}: '{}'", j, getCellValueAsString(cell));
                    }
                }

                try {
                    String prenom = getCellValueAsString(row.getCell(0));
                    String nom = getCellValueAsString(row.getCell(1));
                    String matricule = getCellValueAsString(row.getCell(2));
                    String email = getCellValueAsString(row.getCell(3));
                    String telephone = getCellValueAsString(row.getCell(4));

                    log.info("Row {} - Essential fields: prenom='{}', nom='{}', matricule='{}', email='{}'",
                            i, prenom, nom, matricule, email);

                    String passwordPlain = null;
                    int dateIndex = 5;
                    if (hasPasswordColumn) {
                        passwordPlain = getCellValueAsString(row.getCell(5));
                        dateIndex = 6;
                        log.info("Row {} - Password column found: '{}'", i, passwordPlain);
                    }

                    String dateNaissanceStr = getCellValueAsString(row.getCell(dateIndex));
                    String lieuNaissance = getCellValueAsString(row.getCell(dateIndex + 1));
                    String sexeStr = getCellValueAsString(row.getCell(dateIndex + 2));
                    String nationalite = getCellValueAsString(row.getCell(dateIndex + 3));
                    String adresse = getCellValueAsString(row.getCell(dateIndex + 4));
                    String ville = getCellValueAsString(row.getCell(dateIndex + 5));
                    String situationFamiliale = getCellValueAsString(row.getCell(dateIndex + 6));
                    String niveauStr = getCellValueAsString(row.getCell(dateIndex + 7));
                    String groupe = getCellValueAsString(row.getCell(dateIndex + 8));
                    String anneeAcademique = getCellValueAsString(row.getCell(dateIndex + 9));
                    String statutStr = getCellValueAsString(row.getCell(dateIndex + 10));
                    String bourseStr = getCellValueAsString(row.getCell(dateIndex + 11));
                    String handicapStr = getCellValueAsString(row.getCell(dateIndex + 12));

                    if (isNullOrEmpty(matricule) || isNullOrEmpty(nom) ||
                            isNullOrEmpty(prenom) || isNullOrEmpty(email)) {
                        log.warn("Row {} skipped - missing essential fields", i);
                        continue;
                    }

                    LocalDate dateNaissance = null;
                    if (!isNullOrEmpty(dateNaissanceStr)) {
                        try {
                            dateNaissance = LocalDate.parse(dateNaissanceStr, DATE_FORMATTER);
                            log.info("Row {} - Date parsed successfully: {}", i, dateNaissance);
                        } catch (DateTimeParseException e) {
                            log.error("Row {} - Invalid date format '{}', skipping row", i, dateNaissanceStr);
                            continue;
                        }
                    }

                    Sex sexe = Sex.male;
                    if (!isNullOrEmpty(sexeStr) &&
                            (sexeStr.equalsIgnoreCase("female") || sexeStr.equalsIgnoreCase("F"))) {
                        sexe = Sex.female;
                    }
                    log.info("Row {} - Sex: {}", i, sexe);

                    Niveau niveau = null;
                    if (!isNullOrEmpty(niveauStr)) {
                        try {
                            niveau = Niveau.valueOf(niveauStr.trim());
                            log.info("Row {} - Niveau parsed: {}", i, niveau);
                        } catch (IllegalArgumentException e) {
                            log.error("Row {} - Invalid niveau '{}', skipping row", i, niveauStr);
                            continue;
                        }
                    }

                    Statut statut = Statut.Actif;
                    if (!isNullOrEmpty(statutStr)) {
                        try {
                            statut = Statut.valueOf(statutStr.trim());
                            log.info("Row {} - Statut parsed: {}", i, statut);
                        } catch (IllegalArgumentException e) {
                            log.warn("Row {} - Invalid statut '{}', using default Actif", i, statutStr);
                        }
                    }

                    YesOrNo bourse = YesOrNo.No;
                    if (!isNullOrEmpty(bourseStr) &&
                            (bourseStr.equalsIgnoreCase("Yes") || bourseStr.equalsIgnoreCase("Oui") ||
                                    bourseStr.equalsIgnoreCase("Y") || bourseStr.equals("1"))) {
                        bourse = YesOrNo.Yes;
                    }

                    YesOrNo handicap = YesOrNo.No;
                    if (!isNullOrEmpty(handicapStr) &&
                            (handicapStr.equalsIgnoreCase("Yes") || handicapStr.equalsIgnoreCase("Oui") ||
                                    handicapStr.equalsIgnoreCase("Y") || handicapStr.equals("1"))) {
                        handicap = YesOrNo.Yes;
                    }

                    String passwordToUse = isNullOrEmpty(passwordPlain) ? matricule.trim() : passwordPlain.trim();
                    String hashedPassword = PASSWORD_ENCODER.encode(passwordToUse);
                    log.info("Row {} - Password will be: {}", i, passwordToUse);

                    User user = User.builder()
                            .email(email.trim())
                            .passwordHash(hashedPassword)
                            .role(Role.ETUDIANT)
                            .nom(nom.trim())
                            .prenom(prenom.trim())
                            .telephone(telephone != null ? telephone.trim() : "")
                            .image(null)
                            .build();

                    Student student = Student.builder()
                            .user(user)
                            .matricule(matricule.trim())
                            .dateNaissance(dateNaissance)
                            .lieuNaissance(lieuNaissance != null ? lieuNaissance.trim() : null)
                            .sexe(sexe)
                            .nationalite(nationalite != null ? nationalite.trim() : "Unknown")
                            .adresse(adresse != null ? adresse.trim() : null)
                            .ville(ville != null ? ville.trim() : "Unknown")
                            .situationFamiliale(situationFamiliale != null ? situationFamiliale.trim() : "Single")
                            .niveau(niveau)
                            .groupe(groupe != null ? groupe.trim() : "A")
                            .anneeAcademique(anneeAcademique != null ? anneeAcademique.trim() : null)
                            .statut(statut)
                            .bourse(bourse)
                            .handicap(handicap)
                            .build();

                    students.add(student);
                    log.info("Row {} - Student successfully created: {} {}", i, prenom, nom);
                } catch (Exception e) {
                    log.error("Error importing student from row {}: {}", i, e.getMessage(), e);
                }
            }

            log.info("Import completed - Processed rows: {}, Successful imports: {}", processedRows, students.size());
        } catch (Exception ex) {
            log.error("Failed to import Excel file", ex);
            throw new RuntimeException("Failed to import Excel file", ex);
        }
        return students;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = 0; c < 4; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK &&
                    !isNullOrEmpty(getCellValueAsString(cell))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    public static byte[] exportStudents(List<Student> students) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Students");
            Row header = sheet.createRow(0);
            String[] columns = {"prenom","nom","matricule","email","telephone","dateNaissance","lieuNaissance","sexe","nationalite","adresse","ville","situationFamiliale","niveau","groupe","anneeAcademique","statut","bourse","handicap"};
            for(int i=0;i<columns.length;i++) header.createCell(i).setCellValue(columns[i]);
            int rowNum = 1;
            for(Student s:students){
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getUser().getPrenom());
                row.createCell(1).setCellValue(s.getUser().getNom());
                row.createCell(2).setCellValue(s.getMatricule());
                row.createCell(3).setCellValue(s.getUser().getEmail());
                row.createCell(4).setCellValue(s.getUser().getTelephone());
                row.createCell(5).setCellValue(s.getDateNaissance()!=null?s.getDateNaissance().toString():"");
                row.createCell(6).setCellValue(s.getLieuNaissance()!=null?s.getLieuNaissance():"");
                row.createCell(7).setCellValue(s.getSexe()!=null?s.getSexe().name():"");
                row.createCell(8).setCellValue(s.getNationalite()!=null?s.getNationalite():"");
                row.createCell(9).setCellValue(s.getAdresse()!=null?s.getAdresse():"");
                row.createCell(10).setCellValue(s.getVille()!=null?s.getVille():"");
                row.createCell(11).setCellValue(s.getSituationFamiliale()!=null?s.getSituationFamiliale():"");
                row.createCell(12).setCellValue(s.getNiveau()!=null?s.getNiveau().name():"");
                row.createCell(13).setCellValue(s.getGroupe()!=null?s.getGroupe():"");
                row.createCell(14).setCellValue(s.getAnneeAcademique()!=null?s.getAnneeAcademique():"");
                row.createCell(15).setCellValue(s.getStatut()!=null?s.getStatut().name():"");
                row.createCell(16).setCellValue(s.getBourse()!=null?s.getBourse().name():"");
                row.createCell(17).setCellValue(s.getHandicap()!=null?s.getHandicap().name():"");
            }
            for(int i=0;i<columns.length;i++) sheet.autoSizeColumn(i);
            workbook.write(baos);
            return baos.toByteArray();
        } catch(Exception ex){throw new RuntimeException("Failed to export Excel file", ex);}
    }
}