package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarResponseDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.services.ResponsavelBuscarService;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/responsaveis-buscar")
@Tag(name = "Controle de Responsáveis Buscar", description = "Gerencia quem pode buscar o aluno")
public class ResponsavelBuscarController {

    @Autowired
    private ResponsavelBuscarService responsavelBuscarService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar responsável de busca por ID")
    public ResponseEntity<?> buscarResponsavelBuscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            return ResponseEntity.ok(responsavelBuscarService.buscarResponsavelBuscarPorId(id));

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os responsáveis de busca ativos")
    public ResponseEntity<?> listarTodosResponsaveisBuscar(@AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        if (!autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        try {
            return ResponseEntity.ok(responsavelBuscarService.listarTodosResponsaveisBuscar());
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PostMapping("/cliente/{clienteId}")
    @Operation(summary = "Vincular novo responsável de busca")
    public ResponseEntity<?> criarResponsavelBuscar(
            @PathVariable Long clienteId,
            @Valid @RequestBody ResponsavelBuscarRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responsavelBuscarService.criarResponsavelBuscar(clienteId, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar responsável de busca")
    public ResponseEntity<?> atualizarResponsavelBuscar(
            @PathVariable Long id,
            @Valid @RequestBody ResponsavelBuscarRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            return ResponseEntity.ok(responsavelBuscarService.atualizarResponsavelBuscar(id, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft Delete de responsável de busca")
    public ResponseEntity<?> deletarResponsavelBuscar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            responsavelBuscarService.deletarResponsavelBuscar(id);
            return ResponseEntity.ok(ResponseUtil.response("Responsável de busca removido com sucesso."));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}