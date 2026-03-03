package br.com.abadeus.presentation;

import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioRequestDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.application.services.UsuariosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<UsuarioResponseDTO> listarPorId(
            @PathVariable long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO usarioLogado) {

        try {
            UsuarioResponseDTO usuarioResponseDTO = usuariosService.listarPorId(id);
            return ResponseEntity.ok(usuarioResponseDTO);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos usuários")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosUsuarios(
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {

        try {
            List<UsuarioResponseDTO> listarUsuarios = usuariosService.listarTodosUsuarios();
            return ResponseEntity.ok(listarUsuarios);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar usuário")
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        try {
            UsuarioResponseDTO usuarioResponse = usuariosService.criarUsuario(usuarioRequestDTO);

            return ResponseEntity.ok(usuarioResponse);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO usuarioRequestDTO,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {

        try {
            UsuarioResponseDTO usuarioResponse = usuariosService.salvarUsuario(id, usuarioRequestDTO, usuarioLogado);
            return ResponseEntity.ok(usuarioResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}