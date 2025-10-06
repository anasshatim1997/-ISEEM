package com.iseem_backend.application.utils;

import com.iseem_backend.application.enums.*;
import com.iseem_backend.application.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ExcelUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(8, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(17, 0);

    public static byte[] exportEnseignants(List<Enseignant> enseignants) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Enseignants");
            String[] columns = {
                    "prenom", "nom", "email", "telephone", "specialite",
                    "dateEmbauche", "statusEnseignant", "heuresTravail",
                    "horaireDebut", "horaireFin"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Enseignant e : enseignants) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getUser().getPrenom());
                row.createCell(1).setCellValue(e.getUser().getNom());
                row.createCell(2).setCellValue(e.getUser().getEmail());
                row.createCell(3).setCellValue(e.getUser().getTelephone());
                row.createCell(4).setCellValue(e.getSpecialite());
                row.createCell(5).setCellValue(e.getDateEmbauche() != null ? e.getDateEmbauche().toString() : "");
                row.createCell(6).setCellValue(e.getStatusEnseignant() != null ? e.getStatusEnseignant().name() : "");
                row.createCell(7).setCellValue(e.getHeuresTravail() != null ? e.getHeuresTravail().toHours() : 0);
                row.createCell(8).setCellValue(e.getHoraire() != null && e.getHoraire().getStartTime() != null ? e.getHoraire().getStartTime().toString() : "");
                row.createCell(9).setCellValue(e.getHoraire() != null && e.getHoraire().getEndTime() != null ? e.getHoraire().getEndTime().toString() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static List<Enseignant> importEnseignants(MultipartFile file) {
        List<Enseignant> enseignants = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            log.info("Starting enseignant import - Total rows: {}", totalRows);

            int passwordColumnIndex = findPasswordColumnIndex(sheet.getRow(0));
            log.info("Has password column: {}, at index: {}", passwordColumnIndex >= 0, passwordColumnIndex);

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);

                if (isRowEmpty(row)) {
                    log.info("Row {} is empty, skipping", i);
                    continue;
                }

                try {
                    Enseignant enseignant = parseEnseignantRow(row, passwordColumnIndex, i);
                    if (enseignant != null) {
                        enseignants.add(enseignant);
                        log.info("Row {} - Enseignant successfully created: {} {}", i,
                                enseignant.getUser().getPrenom(), enseignant.getUser().getNom());
                    }
                } catch (Exception e) {
                    log.error("Error importing enseignant from row {}: {}", i, e.getMessage(), e);
                }
            }

            log.info("Import completed - Successful imports: {}", enseignants.size());
        } catch (Exception ex) {
            log.error("Failed to import Excel file", ex);
            throw new RuntimeException("Failed to import Excel file", ex);
        }
        return enseignants;
    }

    private static Enseignant parseEnseignantRow(Row row, int passwordColumnIndex, int rowNum) {
        String prenom = getCellValueAsString(row.getCell(0));
        String nom = getCellValueAsString(row.getCell(1));
        String email = getCellValueAsString(row.getCell(2));
        String telephone = getCellValueAsString(row.getCell(3));
        String specialite = getCellValueAsString(row.getCell(4));

        int baseIndex = 5;
        String passwordPlain = null;

        if (passwordColumnIndex > 4) {
            passwordPlain = getCellValueAsString(row.getCell(passwordColumnIndex));
            baseIndex = passwordColumnIndex + 1;
        }

        String dateEmbaucheStr = getCellValueAsString(row.getCell(baseIndex));
        String statusStr = getCellValueAsString(row.getCell(baseIndex + 1));
        String heuresTravailStr = getCellValueAsString(row.getCell(baseIndex + 2));
        String horaireDebutStr = getCellValueAsString(row.getCell(baseIndex + 3));
        String horaireFinStr = getCellValueAsString(row.getCell(baseIndex + 4));

        if (isNullOrEmpty(email) || isNullOrEmpty(nom) || isNullOrEmpty(prenom) || isNullOrEmpty(specialite)) {
            log.warn("Row {} skipped - missing essential fields", rowNum);
            return null;
        }

        LocalDate dateEmbauche = parseDate(dateEmbaucheStr, rowNum);
        StatusEnseignant status = parseStatusEnseignant(statusStr, rowNum);
        Duration heuresTravail = parseHeuresTravail(heuresTravailStr, rowNum);
        TimeSlot horaire = parseHoraire(horaireDebutStr, horaireFinStr, rowNum);

        String passwordToUse = isNullOrEmpty(passwordPlain) ? email.split("@")[0] : passwordPlain.trim();
        String hashedPassword = PASSWORD_ENCODER.encode(passwordToUse);

        log.info("Row {} - Creating enseignant with email: {}, password will be: {}", rowNum, email, passwordToUse);

        User user = User.builder()
                .email(email.trim())
                .passwordHash(hashedPassword)
                .role(Role.ENSEIGNANT)
                .nom(nom.trim())
                .prenom(prenom.trim())
                .telephone(telephone != null ? telephone.trim() : "")
                .image(null)
                .build();

        return Enseignant.builder()
                .user(user)
                .specialite(specialite.trim())
                .dateEmbauche(dateEmbauche != null ? dateEmbauche : LocalDate.now())
                .statusEnseignant(status)
                .heuresTravail(heuresTravail)
                .horaire(horaire)
                .build();
    }

    private static int findPasswordColumnIndex(Row headerRow) {
        if (headerRow == null) {
            return -1;
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String header = getCellValueAsString(headerRow.getCell(i));
            if (header != null && isPasswordHeader(header)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isPasswordHeader(String header) {
        String lowerHeader = header.toLowerCase();
        return lowerHeader.equals("password") || lowerHeader.equals("motdepasse") || lowerHeader.equals("pwd");
    }

    private static LocalDate parseDate(String dateStr, int rowNum) {
        if (isNullOrEmpty(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Row {} - Invalid date format '{}', using current date", rowNum, dateStr);
            return LocalDate.now();
        }
    }

    private static StatusEnseignant parseStatusEnseignant(String statusStr, int rowNum) {
        if (isNullOrEmpty(statusStr)) {
            return StatusEnseignant.permanent;
        }
        try {
            return StatusEnseignant.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Row {} - Invalid status '{}', using default permanent", rowNum, statusStr);
            return StatusEnseignant.permanent;
        }
    }

    private static Duration parseHeuresTravail(String heuresStr, int rowNum) {
        if (isNullOrEmpty(heuresStr)) {
            return null;
        }
        try {
            long hours = Long.parseLong(heuresStr.trim());
            return Duration.ofHours(hours);
        } catch (NumberFormatException e) {
            log.warn("Row {} - Invalid hours format '{}', skipping heuresTravail", rowNum, heuresStr);
            return null;
        }
    }

    private static TimeSlot parseHoraire(String debutStr, String finStr, int rowNum) {
        if (isNullOrEmpty(debutStr) || isNullOrEmpty(finStr)) {
            return createDefaultTimeSlot();
        }

        try {
            LocalTime debut = LocalTime.parse(debutStr, TIME_FORMATTER);
            LocalTime fin = LocalTime.parse(finStr, TIME_FORMATTER);
            return createTimeSlot(debut, fin);
        } catch (DateTimeParseException e) {
            log.warn("Row {} - Invalid time format, using default 08:00-17:00", rowNum);
            return createDefaultTimeSlot();
        }
    }

    private static TimeSlot createTimeSlot(LocalTime start, LocalTime end) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(start);
        timeSlot.setEndTime(end);
        return timeSlot;
    }

    private static TimeSlot createDefaultTimeSlot() {
        return createTimeSlot(DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    public static byte[] exportDiplomes(List<Diplome> diplomes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Diplomes");
            String[] columns = {
                    "ID", "NomDiplome", "TypeDiplome", "AnneeObtention", "EstValide",
                    "Mention", "DateDelivrance", "SignatureAdmin", "QrCodeUrl", "Commentaire", "ModeRemise"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

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

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static List<Diplome> importDiplomes(MultipartFile file) throws IOException {
        List<Diplome> diplomes = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                Diplome d = new Diplome();
                d.setNomDiplome(getCellValueAsString(row.getCell(1)));
                d.setTypeDiplome(parseEnum(row.getCell(2), com.iseem_backend.application.enums.TypeDiplome.class));
                d.setAnneeObtention(getCellValueAsInteger(row.getCell(3)));
                d.setEstValide(getCellValueAsBoolean(row.getCell(4)));
                d.setMention(parseEnum(row.getCell(5), com.iseem_backend.application.enums.Mention.class));
                d.setDateDelivrance(parseCellDate(row.getCell(6)));
                d.setQrCodeUrl(getCellValueAsString(row.getCell(8)));
                d.setCommentaire(getCellValueAsString(row.getCell(9)));
                d.setModeRemise(parseEnum(row.getCell(10), com.iseem_backend.application.enums.ModeRemise.class));
                diplomes.add(d);
            }
        }
        return diplomes;
    }

    public static byte[] exportFormations(List<Formation> formations) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static List<Formation> importFormations(MultipartFile file) throws IOException {
        List<Formation> formations = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                Formation f = new Formation();
                f.setNom(getCellValueAsString(row.getCell(1)));
                f.setDuree(getCellValueAsInteger(row.getCell(2)));
                f.setCout(BigDecimal.valueOf(getCellValueAsDouble(row.getCell(3))));
                f.setDescription(getCellValueAsString(row.getCell(5)));
                f.setAnneeFormation(getCellValueAsString(row.getCell(6)));
                f.setModeFormation(parseEnum(row.getCell(7), com.iseem_backend.application.enums.ModeFormation.class));
                f.setNiveauAcces(getCellValueAsString(row.getCell(8)));
                f.setCapaciteMax(getCellValueAsInteger(row.getCell(9)));
                f.setEstActive(getCellValueAsBoolean(row.getCell(10)));
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

            int passwordColumnIndex = findPasswordColumnIndex(sheet.getRow(0));
            boolean hasPasswordColumn = passwordColumnIndex >= 0;

            log.info("Header row exists: {}, Has password column: {}", sheet.getRow(0) != null, hasPasswordColumn);

            int processedRows = 0;
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                log.info("Processing row {}: row exists = {}", i, row != null);

                if (isRowEmpty(row)) {
                    log.info("Row {} is empty, skipping", i);
                    continue;
                }

                processedRows++;

                try {
                    Student student = parseStudentRow(row, hasPasswordColumn, passwordColumnIndex, i);
                    if (student != null) {
                        students.add(student);
                        log.info("Row {} - Student successfully created: {} {}", i,
                                student.getUser().getPrenom(), student.getUser().getNom());
                    }
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

    private static Student parseStudentRow(Row row, boolean hasPasswordColumn, int passwordColumnIndex, int rowNum) {
        String prenom = getCellValueAsString(row.getCell(0));
        String nom = getCellValueAsString(row.getCell(1));
        String matricule = getCellValueAsString(row.getCell(2));
        String email = getCellValueAsString(row.getCell(3));
        String telephone = getCellValueAsString(row.getCell(4));

        log.info("Row {} - Essential fields: prenom='{}', nom='{}', matricule='{}', email='{}'",
                rowNum, prenom, nom, matricule, email);

        String passwordPlain = null;
        int dateIndex = 5;
        if (hasPasswordColumn) {
            passwordPlain = getCellValueAsString(row.getCell(passwordColumnIndex));
            dateIndex = passwordColumnIndex + 1;
            log.info("Row {} - Password column found: '{}'", rowNum, passwordPlain);
        }

        if (isNullOrEmpty(matricule) || isNullOrEmpty(nom) || isNullOrEmpty(prenom) || isNullOrEmpty(email)) {
            log.warn("Row {} skipped - missing essential fields", rowNum);
            return null;
        }

        StudentData data = extractStudentData(row, dateIndex, rowNum);
        if (data == null) {
            return null;
        }

        String passwordToUse = isNullOrEmpty(passwordPlain) ? matricule.trim() : passwordPlain.trim();
        String hashedPassword = PASSWORD_ENCODER.encode(passwordToUse);
        log.info("Row {} - Password will be: {}", rowNum, passwordToUse);

        User user = User.builder()
                .email(email.trim())
                .passwordHash(hashedPassword)
                .role(Role.ETUDIANT)
                .nom(nom.trim())
                .prenom(prenom.trim())
                .telephone(telephone != null ? telephone.trim() : "")
                .image(null)
                .build();

        return Student.builder()
                .user(user)
                .matricule(matricule.trim())
                .dateNaissance(data.dateNaissance)
                .lieuNaissance(data.lieuNaissance)
                .sexe(data.sexe)
                .nationalite(data.nationalite)
                .adresse(data.adresse)
                .ville(data.ville)
                .situationFamiliale(data.situationFamiliale)
                .niveau(data.niveau)
                .groupe(data.groupe)
                .anneeAcademique(data.anneeAcademique)
                .statut(data.statut)
                .bourse(data.bourse)
                .handicap(data.handicap)
                .build();
    }

    private static StudentData extractStudentData(Row row, int dateIndex, int rowNum) {
        StudentData data = new StudentData();

        String dateNaissanceStr = getCellValueAsString(row.getCell(dateIndex));
        data.lieuNaissance = getCellValueAsString(row.getCell(dateIndex + 1));
        String sexeStr = getCellValueAsString(row.getCell(dateIndex + 2));
        data.nationalite = getCellValueAsString(row.getCell(dateIndex + 3));
        data.adresse = getCellValueAsString(row.getCell(dateIndex + 4));
        data.ville = getCellValueAsString(row.getCell(dateIndex + 5));
        data.situationFamiliale = getCellValueAsString(row.getCell(dateIndex + 6));
        String niveauStr = getCellValueAsString(row.getCell(dateIndex + 7));
        data.groupe = getCellValueAsString(row.getCell(dateIndex + 8));
        data.anneeAcademique = getCellValueAsString(row.getCell(dateIndex + 9));
        String statutStr = getCellValueAsString(row.getCell(dateIndex + 10));
        String bourseStr = getCellValueAsString(row.getCell(dateIndex + 11));
        String handicapStr = getCellValueAsString(row.getCell(dateIndex + 12));

        if (!isNullOrEmpty(dateNaissanceStr)) {
            try {
                data.dateNaissance = LocalDate.parse(dateNaissanceStr, DATE_FORMATTER);
                log.info("Row {} - Date parsed successfully: {}", rowNum, data.dateNaissance);
            } catch (DateTimeParseException e) {
                log.error("Row {} - Invalid date format '{}', skipping row", rowNum, dateNaissanceStr);
                return null;
            }
        }

        data.sexe = parseSex(sexeStr);
        log.info("Row {} - Sex: {}", rowNum, data.sexe);

        data.niveau = parseNiveau(niveauStr, rowNum);
        if (data.niveau == null) {
            return null;
        }

        data.statut = parseStatut(statutStr, rowNum);
        data.bourse = parseYesNo(bourseStr);
        data.handicap = parseYesNo(handicapStr);

        data.lieuNaissance = data.lieuNaissance != null ? data.lieuNaissance.trim() : null;
        data.nationalite = data.nationalite != null ? data.nationalite.trim() : "Unknown";
        data.adresse = data.adresse != null ? data.adresse.trim() : null;
        data.ville = data.ville != null ? data.ville.trim() : "Unknown";
        data.situationFamiliale = data.situationFamiliale != null ? data.situationFamiliale.trim() : "Single";
        data.groupe = data.groupe != null ? data.groupe.trim() : "A";
        data.anneeAcademique = data.anneeAcademique != null ? data.anneeAcademique.trim() : null;

        return data;
    }

    private static Sex parseSex(String sexeStr) {
        if (!isNullOrEmpty(sexeStr) && (sexeStr.equalsIgnoreCase("female") || sexeStr.equalsIgnoreCase("F"))) {
            return Sex.female;
        }
        return Sex.male;
    }

    private static Niveau parseNiveau(String niveauStr, int rowNum) {
        if (isNullOrEmpty(niveauStr)) {
            return null;
        }
        try {
            Niveau niveau = Niveau.valueOf(niveauStr.trim());
            log.info("Row {} - Niveau parsed: {}", rowNum, niveau);
            return niveau;
        } catch (IllegalArgumentException e) {
            log.error("Row {} - Invalid niveau '{}', skipping row", rowNum, niveauStr);
            return null;
        }
    }

    private static Statut parseStatut(String statutStr, int rowNum) {
        if (isNullOrEmpty(statutStr)) {
            return Statut.Actif;
        }
        try {
            Statut statut = Statut.valueOf(statutStr.trim());
            log.info("Row {} - Statut parsed: {}", rowNum, statut);
            return statut;
        } catch (IllegalArgumentException e) {
            log.warn("Row {} - Invalid statut '{}', using default Actif", rowNum, statutStr);
            return Statut.Actif;
        }
    }

    private static YesOrNo parseYesNo(String value) {
        if (!isNullOrEmpty(value) && (value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("Oui") ||
                value.equalsIgnoreCase("Y") || value.equals("1"))) {
            return YesOrNo.Yes;
        }
        return YesOrNo.No;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = 0; c < 4; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !isNullOrEmpty(getCellValueAsString(cell))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

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
            default:
                return null;
        }
    }

    private static Integer getCellValueAsInteger(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }

    private static Double getCellValueAsDouble(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0.0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        return 0.0;
    }

    private static Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return false;
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        return false;
    }

    private static LocalDate parseCellDate(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }

    private static <E extends Enum<E>> E parseEnum(Cell cell, Class<E> enumClass) {
        String value = getCellValueAsString(cell);
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static byte[] exportStudents(List<Student> students) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Students");
            Row header = sheet.createRow(0);
            String[] columns = {
                    "prenom", "nom", "matricule", "email", "telephone", "dateNaissance",
                    "lieuNaissance", "sexe", "nationalite", "adresse", "ville",
                    "situationFamiliale", "niveau", "groupe", "anneeAcademique",
                    "statut", "bourse", "handicap"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getUser().getPrenom());
                row.createCell(1).setCellValue(s.getUser().getNom());
                row.createCell(2).setCellValue(s.getMatricule());
                row.createCell(3).setCellValue(s.getUser().getEmail());
                row.createCell(4).setCellValue(s.getUser().getTelephone());
                row.createCell(5).setCellValue(s.getDateNaissance() != null ? s.getDateNaissance().toString() : "");
                row.createCell(6).setCellValue(s.getLieuNaissance() != null ? s.getLieuNaissance() : "");
                row.createCell(7).setCellValue(s.getSexe() != null ? s.getSexe().name() : "");
                row.createCell(8).setCellValue(s.getNationalite() != null ? s.getNationalite() : "");
                row.createCell(9).setCellValue(s.getAdresse() != null ? s.getAdresse() : "");
                row.createCell(10).setCellValue(s.getVille() != null ? s.getVille() : "");
                row.createCell(11).setCellValue(s.getSituationFamiliale() != null ? s.getSituationFamiliale() : "");
                row.createCell(12).setCellValue(s.getNiveau() != null ? s.getNiveau().name() : "");
                row.createCell(13).setCellValue(s.getGroupe() != null ? s.getGroupe() : "");
                row.createCell(14).setCellValue(s.getAnneeAcademique() != null ? s.getAnneeAcademique() : "");
                row.createCell(15).setCellValue(s.getStatut() != null ? s.getStatut().name() : "");
                row.createCell(16).setCellValue(s.getBourse() != null ? s.getBourse().name() : "");
                row.createCell(17).setCellValue(s.getHandicap() != null ? s.getHandicap().name() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to export Excel file", ex);
        }
    }

    private static class StudentData {
        LocalDate dateNaissance;
        String lieuNaissance;
        Sex sexe;
        String nationalite;
        String adresse;
        String ville;
        String situationFamiliale;
        Niveau niveau;
        String groupe;
        String anneeAcademique;
        Statut statut;
        YesOrNo bourse;
        YesOrNo handicap;
    }
}