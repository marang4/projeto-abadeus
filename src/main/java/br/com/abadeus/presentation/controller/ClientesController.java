package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.cliente.ClienteRequestDTO;
import br.com.abadeus.application.dto.cliente.ClienteResponseDTO;
import br.com.abadeus.application.dto.cliente.ClienteUpdateDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.services.ClientesService;
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

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Controle de Clientes", description = "Gerencia o CRUD de clientes")
public class ClientesController {

    @Autowired
    private ClientesService clientesService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar cliente por ID")
    public ResponseEntity<?> listarClientePorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {
        try {
            return ResponseEntity.ok(clientesService.listarClientePorId(id));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos clientes")
    public ResponseEntity<?> listarTodosClientes(
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        if (!autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        try {
            return ResponseEntity.ok(clientesService.listarTodosClientes());
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Criar cliente (Cadastro Público)")
    public ResponseEntity<?> criarCliente(
            @Valid @RequestBody ClienteRequestDTO clienteRequest) {
        try {
            ClienteResponseDTO response = clientesService.criarCliente(clienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente (Requer Login)")
    public ResponseEntity<?> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDTO clienteUpdate,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        if (!autenticacao.isAdmin() && !autenticacao.id().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado. Você não tem permissão para alterar este cliente."));
        }

        try {
            ClienteResponseDTO response = clientesService.atualizarCliente(id, clienteUpdate);
            return ResponseEntity.ok(response);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente (Soft Delete - Requer Admin)")
    public ResponseEntity<?> deletarCliente(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        if (!autenticacao.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.response("Acesso negado."));
        }

        try {
            clientesService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }
}