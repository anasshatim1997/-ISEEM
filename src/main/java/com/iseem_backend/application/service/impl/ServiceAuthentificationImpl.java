package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.DemandeAuthentification;
import com.iseem_backend.application.DTO.response.ReponseAuthentification;
import com.iseem_backend.application.mapper.UserMapper;
import com.iseem_backend.application.service.ServiceAuthentification;
import com.iseem_backend.application.config.JwtUtil;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceAuthentificationImpl implements ServiceAuthentification {
    private final UserRepository userRepository;
    private final PasswordEncoder encodeurMotDePasse;
    private final JwtUtil jwtUtil;
    private final UserMapper  userMapper;

    @Override
    @Transactional
    public ReponseAuthentification authentifier(DemandeAuthentification demande) {

        User user = userRepository.findUserByEmail(demande.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!encodeurMotDePasse.matches(demande.getMotDePasse(), user.getPasswordHash())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        String token = jwtUtil.genererToken(user);

        return new ReponseAuthentification(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getUserId().toString()
        );
    }



}
