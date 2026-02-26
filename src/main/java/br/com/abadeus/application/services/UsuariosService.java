package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.usuario.UsuarioRequestDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.domain.entity.Usuarios;
import br.com.abadeus.domain.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List; // Import necessário
import java.util.stream.Collectors;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO listarPorId(Long id) {
        return usuariosRepository.findById(id)
                .map(UsuarioResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public List<UsuarioResponseDTO> listarTodosUsuarios() {
        return usuariosRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        if (usuariosRepository.existsByEmail(usuarioRequestDTO.email())) {
            throw new RuntimeException("Email já cadastrado.");
        }

        Usuarios novoUsuario = new Usuarios(usuarioRequestDTO);
        novoUsuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));
        novoUsuario.setRole("ROLE_USER");
        novoUsuario.setDataCriacao(LocalDateTime.now());

        usuariosRepository.save(novoUsuario);
        return novoUsuario.toDtoResponse();
    }
}