package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.services.EnderecoService;
import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.entity.Enderecos;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.ClientesRepository;
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
@RequestMapping("/endereco")
@Tag(name = "Controle de Endereços", description = "Gerencia o CRUD de endereços")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ClientesRepository clientesRepository;

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
    @Operation(summary = "Criar endereço")
    public ResponseEntity<?> criarEndereco(
            @Valid @RequestBody EnderecoRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {
        try {
            Enderecos enderecoSalvo = enderecoService.criarEndereco(dto);

            if (autenticacao != null && "ROLE_CLIENTE".equalsIgnoreCase(autenticacao.role())) {
                Clientes cliente = clientesRepository
                        .findByUsuarioEmail(autenticacao.email())
                        .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));

                cliente.setEndereco(enderecoSalvo);
                clientesRepository.save(cliente);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new EnderecoResponseDTO(enderecoSalvo));

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseUtil.response("Erro ao processar endereço: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar endereço existente")
    public ResponseEntity<?> atualizarEndereco(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {
        try {
            return ResponseEntity.ok(enderecoService.atualizarEndereco(id, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}