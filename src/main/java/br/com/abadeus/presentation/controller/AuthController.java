package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.auth.*;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.application.services.AuthService;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Controller Autenticação", description = "Login, Recuperação e Troca de Senha Unificados")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            UsuarioResponseDTO userDto = authService.realizarLogin(loginRequest, response);
            return ResponseEntity.ok(userDto);

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(determinarStatusErro(e.getMessage()))
                    .body(ResponseUtil.response(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.response("Erro interno no servidor."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.realizarLogout(response);

        return ResponseEntity.ok(ResponseUtil.response("Logout realizado com sucesso."));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário logado", description = "Lê o cookie de sessão e retorna os dados do perfil.")
    public ResponseEntity<?> getMe() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(principal);
    }

    @PostMapping("/recuperarsenha")
    @Operation(summary = "Esqueci Minha Senha", description = "Dispara email se o cadastro existir.")
    public ResponseEntity<?> gerarLinkRecuperacao(@RequestBody RecuperarSenhaDTO recuperarSenhaRequest) {
        try {
            authService.gerarLinkRecuperacao(recuperarSenhaRequest);
            return ResponseEntity.ok(ResponseUtil.response("Se o e-mail estiver cadastrado, você receberá um link em breve."));

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(determinarStatusErro(e.getMessage()))
                    .body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PostMapping("/resetarsenha")
    @Operation(summary = "Redefinir Senha", description = "Redefine senha baseado no token.")
    public ResponseEntity<?> resetarSenha(@RequestBody ResetarSenhaDTO recuperarSenhaRequest) {
        try {
            authService.resetarSenha(recuperarSenhaRequest);
            return ResponseEntity.ok(ResponseUtil.response("Senha alterada com sucesso!"));

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(determinarStatusErro(e.getMessage()))
                    .body(ResponseUtil.response(e.getMessage()));
        }
    }


    @PostMapping("/alterarsenha")
    public ResponseEntity<?> alterarSenha(
            @RequestBody AlterarSenhaDTO recuperarSenhaRequest,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {
        try {
            authService.alterarSenha(autenticacao, recuperarSenhaRequest);
            return ResponseEntity.ok(ResponseUtil.response("Senha alterada com sucesso!"));

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(determinarStatusErro(e.getMessage()))
                    .body(ResponseUtil.response(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.response("Erro ao processar a requisição."));
        }
    }

    private HttpStatus determinarStatusErro(String mensagem) {
        if (mensagem == null) return HttpStatus.BAD_REQUEST; // 400

        String msgLower = mensagem.toLowerCase();

        if (msgLower.contains("credenciais") || msgLower.contains("senha atual incorreta") || msgLower.contains("token inválido")) {
            return HttpStatus.UNAUTHORIZED; // 401
        }
        if (msgLower.contains("não encontrado")) {
            return HttpStatus.NOT_FOUND; // 404
        }
        if (msgLower.contains("permissão") || msgLower.contains("acesso negado")) {
            return HttpStatus.FORBIDDEN; // 403
        }
        if (msgLower.contains("já existe") || msgLower.contains("cadastrado")) {
            return HttpStatus.CONFLICT; // 409
        }

        return HttpStatus.BAD_REQUEST; // 400 padrão
    }
}