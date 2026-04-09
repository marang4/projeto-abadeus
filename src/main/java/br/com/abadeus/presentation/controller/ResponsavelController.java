package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.reponsavel.ResponsavelRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelResponseDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.services.ResponsavelService;
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
@RequestMapping("/responsaveis")
@Tag(name = "Controle de Responsáveis", description = "Gerencia os responsáveis pelos clientes")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar responsável por ID")
    public ResponseEntity<?> buscarResponsavelPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            return ResponseEntity.ok(responsavelService.buscarResponsavelPorId(id));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos os responsáveis ativos")
    public ResponseEntity<?> listarTodosResponsaveis(
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        // TRAVA: Apenas ADMIN lista a base completa
        if (!autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        try {
            return ResponseEntity.ok(responsavelService.listarTodosResponsaveis());
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PostMapping("/cliente/{clienteId}")
    @Operation(summary = "Vincular novo responsável a um cliente")
    public ResponseEntity<?> criarResponsavel(
            @PathVariable Long clienteId,
            @Valid @RequestBody ResponsavelRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(responsavelService.criarResponsavel(clienteId, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de um responsável")
    public ResponseEntity<?> atualizarResponsavel(
            @PathVariable Long id,
            @Valid @RequestBody ResponsavelRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            return ResponseEntity.ok(responsavelService.atualizarResponsavel(id, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão lógica (Soft Delete) de um responsável")
    public ResponseEntity<?> deletarResponsavel(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        try {
            responsavelService.deletarResponsavel(id);
            return ResponseEntity.ok(ResponseUtil.response("Responsável excluído com sucesso."));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}