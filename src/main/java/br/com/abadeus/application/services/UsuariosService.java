package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioRequestDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.domain.entity.Usuarios;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO listarUsuarioPorId(Long id) {
        return usuariosRepository.findById(id)
                .map(UsuarioResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));
    }

    public List<UsuarioResponseDTO> listarTodosUsuarios() {
        return usuariosRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        String emailTratado = dto.email() != null ? dto.email().toLowerCase().trim() : "";

        if (usuariosRepository.existsByEmail(emailTratado)) {
            throw new RegraDeNegocioException("E-mail já cadastrado para outro usuário.");
        }

        Usuarios novoUsuario = new Usuarios(dto);
        novoUsuario.setEmail(emailTratado);
        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));
        novoUsuario.setDataCriacao(LocalDateTime.now());

        usuariosRepository.save(novoUsuario);
        return novoUsuario.toDtoResponse();
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO dto, UsuarioPrincipalDTO autenticacao) {
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));

        if (autenticacao.isAdmin() && !usuario.getId().equals(autenticacao.id())) {
            usuario.setRole(dto.role());
        }

        usuario.setNome(dto.nome());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        Usuarios salvo = usuariosRepository.save(usuario);
        return salvo.toDtoResponse();
    }
}