package br.com.abadeus.presentation;

import br.com.abadeus.application.dto.auth.*;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.application.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Controller Autenticação", description = "Login, Recuperação e Troca de Senha Unificados")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        try {
            UsuarioResponseDTO userDto = authService.realizarLogin(request, response);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        authService.realizarLogout(response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário logado", description = "Lê o cookie de sessão e retorna os dados do perfil.")
    public ResponseEntity<?> getMe() {

        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @PostMapping("/recuperarsenha")
    @Operation(summary = "Esqueci Minha Senha", description = "Dispara email se o cadastro existir.")
    public ResponseEntity<String> gerarLinkRecuperacao(@RequestBody RecuperarSenhaDTO dto) {

        authService.gerarLinkRecuperacao(dto);
        return ResponseEntity.ok("Se o e-mail estiver cadastrado, você receberá um link em breve.");
    }

    @PostMapping("/resetarsenha")
    @Operation(summary = "Redefinir Senha", description = "Redefine senha baseado no token.")
    public ResponseEntity<String> resetarSenha(@RequestBody ResetarSenhaDTO dto) {

        authService.resetarSenha(dto);
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }

    @PostMapping("/alterarsenha")
    public ResponseEntity<String> alterarSenha(
            @RequestBody AlterarSenhaDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {

        authService.alterarSenha(usuarioLogado, dto);
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
}
