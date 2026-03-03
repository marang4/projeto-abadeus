package br.com.abadeus.application.services;

// Adicione a importação do DTO que o JwtFilter espera
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.domain.repository.TokenRepository;
import br.com.abadeus.domain.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${spring.secretkey}") //para buscar a variavel dentro do properties
    private String secret;

    @Value("${spring.tempo_expiracao}")
    private Long tempo;

    private String emissor = "DEVTEST";

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    private Instant gerarDataExpiracao() {
        var dataAtual = LocalDateTime.now();
        dataAtual = dataAtual.plusMinutes(tempo);
        return dataAtual.toInstant(ZoneOffset.of("-03:00"));
    }

    public UsuarioPrincipalDTO validarToken(String token) {
        try {


            return null;

        } catch (Exception exception) {

            return null;
        }
    }
}