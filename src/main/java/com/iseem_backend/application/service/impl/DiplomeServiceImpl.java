package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.DiplomeRequest;
import com.iseem_backend.application.DTO.response.DiplomeResponse;
import com.iseem_backend.application.enums.TypeDiplome;
import com.iseem_backend.application.exceptions.DiplomeNotFoundException;
import com.iseem_backend.application.mapper.DiplomeMapper;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.repository.DiplomeRepository;
import com.iseem_backend.application.repository.UserRepository;
import com.iseem_backend.application.service.DiplomeService;
import com.iseem_backend.application.utils.DiplomePDFGenerator;
import com.iseem_backend.application.utils.ExcelUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiplomeServiceImpl implements DiplomeService {

    private final DiplomeRepository diplomeRepository;
    private final UserRepository userRepository;
    private final DiplomeMapper diplomeMapper;

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public DiplomeResponse creerDiplome(DiplomeRequest request) {
        Diplome diplome = diplomeMapper.toEntity(request);
        diplome = diplomeRepository.save(diplome);
        return diplomeMapper.toDto(diplome);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public DiplomeResponse modifierDiplome(UUID idDiplome, DiplomeRequest request) {
        Diplome diplome = diplomeRepository.findById(idDiplome)
                .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
        diplomeMapper.updateEntityFromRequest(request, diplome);
        diplome = diplomeRepository.save(diplome);
        return diplomeMapper.toDto(diplome);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void validerDiplome(UUID idDiplome) {
        Diplome diplome = diplomeRepository.findById(idDiplome)
                .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
        diplome.setEstValide(true);
        diplomeRepository.save(diplome);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void signerDiplome(UUID idDiplome, UUID adminId) {
        Diplome diplome = diplomeRepository.findById(idDiplome)
                .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        diplome.setSignatureAdmin(admin);
        diplomeRepository.save(diplome);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void supprimerDiplome(UUID idDiplome) {
        Diplome diplome = diplomeRepository.findById(idDiplome)
                .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
        diplomeRepository.delete(diplome);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    @Transactional(readOnly = true)
    public DiplomeResponse obtenirDiplomeParId(UUID idDiplome) {
        Diplome diplome = diplomeRepository.findById(idDiplome)
                .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
        return diplomeMapper.toDto(diplome);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    @Transactional(readOnly = true)
    public List<DiplomeResponse> obtenirTousDiplomes() {
        return diplomeRepository.findAll().stream()
                .map(diplomeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    public List<DiplomeResponse> filtrerDiplomesParType(TypeDiplome typeDiplome) {
        return diplomeRepository.findByTypeDiplome(typeDiplome).stream()
                .map(diplomeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public byte[] exporterDiplomes() {
        try {
            return ExcelUtils.exportDiplomes(diplomeRepository.findAll());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'export des diplômes", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void importerDiplomes(MultipartFile file) {
        try {
            List<Diplome> diplomes = ExcelUtils.importDiplomes(file);
            diplomeRepository.saveAll(diplomes);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'import des diplômes", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    public byte[] genererQRCode(UUID idDiplome) {
        try {
            Diplome diplome = diplomeRepository.findById(idDiplome)
                    .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    "https://diplome.example.com/" + diplome.getIdDiplome(),
                    BarcodeFormat.QR_CODE,
                    250,
                    250
            );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    public byte[] genererPDF(UUID idDiplome) {
        Diplome diplome = diplomeRepository.findById(idDiplome)
                .orElseThrow(() -> new DiplomeNotFoundException(idDiplome));
        return DiplomePDFGenerator.generatePDF(diplome);
    }
}
