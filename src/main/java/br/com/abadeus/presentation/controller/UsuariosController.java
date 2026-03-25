package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioRequestDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.application.services.UsuariosService;
import br.com.abadeus.domain.utils.ResponseUtil;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Controle de Usuários", description = "Gerencia o CRUD de usuários")
public class UsuariosController {

    @Autowired
    private UsuariosService usuariosService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuário por ID")
    public ResponseEntity<?> listarUsuarioPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        // TRAVA: Apenas ADMIN vê detalhes de outros usuários
        if (!autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        try {
            return ResponseEntity.ok(usuariosService.listarUsuarioPorId(id));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos usuários")
    public ResponseEntity<?> listarTodosUsuarios(
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        // TRAVA: Apenas ADMIN lista todo mundo
        if (!autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        return ResponseEntity.ok(usuariosService.listarTodosUsuarios());
    }

    @PostMapping
    @Operation(summary = "Criar usuário")
    public ResponseEntity<?> criarUsuario(
            @RequestBody UsuarioRequestDTO usuarioRequest,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) { // Adicionado o principal aqui!

        // TRAVA: Apenas ADMIN cria novos usuários/admins
        if (autenticacao == null || !autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseUtil.response("Você não tem permissão para criar usuários."));
        }

        try {
            UsuarioResponseDTO usuarioResponse = usuariosService.criarUsuario(usuarioRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO usuarioRequestDTO,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        // TRAVA: Só ADMIN atualiza ou o próprio usuário (opcional)
        if (!autenticacao.isAdmin() && !autenticacao.id().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        try {
            UsuarioResponseDTO usuarioResponse = usuariosService.atualizarUsuario(id, usuarioRequestDTO, autenticacao);
            return ResponseEntity.ok(usuarioResponse);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}