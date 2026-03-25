package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import br.com.abadeus.application.dto.evento.EventoResponseDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.services.EventoService;
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
@RequestMapping("/evento")
@Tag(name = "Controlador de Eventos", description = "Gerencia o CRUD de eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evento por ID")
    public ResponseEntity<?> listarEventoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventoService.listarEventoPorId(id));

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos eventos")
    public ResponseEntity<List<EventoResponseDTO>> listarTodosEventos() {

        return ResponseEntity.ok(eventoService.listarTodosEventos());
    }

    @PostMapping
    @Operation(summary = "Criar eventos")
    public ResponseEntity<?> criarEvento(
            @Valid @RequestBody EventoRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        // TRAVA DE SEGURANÇA: Bloqueia Clientes
        if ("ROLE_CLIENTE".equals(autenticacao.role())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseUtil.response("Clientes não possuem permissão para criar eventos."));
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.criarEvento(dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar evento")
    public ResponseEntity<?> atualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody EventoRequestDTO dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO autenticacao) {

        // TRAVA DE SEGURANÇA: Bloqueia Clientes
        if ("ROLE_CLIENTE".equals(autenticacao.role())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseUtil.response("Clientes não possuem permissão para alterar eventos."));
        }

        try {
            return ResponseEntity.ok(eventoService.atualizarEvento(id, dto));
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}