package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.auth.*;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.domain.entity.Usuarios;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.interfaces.IEnvioEmail;
import br.com.abadeus.domain.repository.UsuariosRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IEnvioEmail envioEmail;

    @Value("${api.frontend.base-url}")
    private String frontendBaseUrl;

    public UsuarioResponseDTO realizarLogin(LoginRequestDTO request, HttpServletResponse response) {

        boolean valido = validarCredenciais(request.email(), request.senha());

        if (!valido) {
            // Lança a exceção customizada; o Controller vai mapear para 401 (UNAUTHORIZED)
            throw new RegraDeNegocioException("Credenciais inválidas.");
        }

        Usuarios user = buscarPorEmail(request.email());
        String token = tokenService.gerarToken(user);

        Cookie authCookie = new Cookie("token", token);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false);
        authCookie.setPath("/");
        authCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(authCookie);

        return new UsuarioResponseDTO(user);
    }

    public void realizarLogout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Transactional
    public void gerarLinkRecuperacao(RecuperarSenhaDTO dto) {
        String email = dto.email().toLowerCase().trim();
        Usuarios user = buscarEmail(email);

        if (user == null) return;

        String token = UUID.randomUUID().toString();
        user.setTokenSenha(token);
        user.setTokenSenhaExpiracao(LocalDateTime.now().plusHours(1));

        salvar(user);

        String link = frontendBaseUrl + "/resetarsenha?token=" + token;
        envioEmail.enviarEmailTemplate(user.getEmail(), "Recuperação de Senha - Logan Express", link, "email-template");
    }

    @Transactional
    public void resetarSenha(ResetarSenhaDTO dto) {
        Usuarios user = buscarToken(dto.token());

        if (user == null || user.getTokenSenhaExpiracao().isBefore(LocalDateTime.now())) {

            throw new RegraDeNegocioException("Token inválido ou expirado.");
        }

        user.setSenha(passwordEncoder.encode(dto.senha()));
        user.setTokenSenha(null);
        user.setTokenSenhaExpiracao(null);

        salvar(user);
    }

    @Transactional
    public void alterarSenha(UsuarioPrincipalDTO usuarioLogado, AlterarSenhaDTO dto) {
        Usuarios user = buscarEmail(usuarioLogado.email().toLowerCase().trim());

        if (user == null) {
            throw new RegraDeNegocioException("Usuário não encontrado.");
        }

        if (!passwordEncoder.matches(dto.senhaAtual(), user.getSenha())) {
            throw new RegraDeNegocioException("Senha atual incorreta.");
        }

        user.setSenha(passwordEncoder.encode(dto.novaSenha()));
        salvar(user);
    }

    public boolean validarCredenciais(String email, String senhaPura) {
        Usuarios user = buscarEmail(email.toLowerCase().trim());
        if (user == null) return false;
        return passwordEncoder.matches(senhaPura, user.getSenha());
    }

    private Usuarios buscarEmail(String email) {
        String emailTratado = (email != null) ? email.trim() : "";
        return usuarioRepository.findByEmailIgnoreCase(emailTratado).orElse(null);
    }

    private Usuarios buscarToken(String token) {
        return usuarioRepository.findByTokenSenha(token).orElse(null);
    }

    public Usuarios buscarPorEmail(String email) {
        return buscarEmail(email);
    }

    private void salvar(Usuarios user) {
        usuarioRepository.save(user);
    }
}