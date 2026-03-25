package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.services.EnderecoService;
import br.com.abadeus.domain.utils.ResponseUtil;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
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
@RequestMapping("/endereco")
@Tag(name = "Controle de Endereços", description = "Gerencia o CRUD de endereços")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar endereço por ID")
    public ResponseEntity<?> listarEnderecoPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {
        try {
            return ResponseEntity.ok(enderecoService.listarEnderecoPorId(id));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos os endereços")
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodosEnderecos() {
        return ResponseEntity.ok(enderecoService.listarTodosEnderecos());
    }

    @PostMapping
    @Operation(summary = "Criar novo endereço")
    public ResponseEntity<?> criarEndereco(
            @Valid @RequestBody EnderecoRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(enderecoService.criarEndereco(dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseUtil.response("Erro ao processar endereço."));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar endereço existente")
    public ResponseEntity<?> atualizarEndereco(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoRequestDTO dto) {
        try {
            return ResponseEntity.ok(enderecoService.atualizarEndereco(id, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}