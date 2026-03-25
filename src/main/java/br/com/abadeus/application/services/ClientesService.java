package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.cliente.ClienteRequestDTO;
import br.com.abadeus.application.dto.cliente.ClienteResponseDTO;
import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.entity.Enderecos;
import br.com.abadeus.domain.entity.Usuarios;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.ClientesRepository;
import br.com.abadeus.domain.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientesService {

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClienteResponseDTO listarClientePorId(Long id) {
        return clientesRepository.findById(id)
                .map(ClienteResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));
    }

    public List<ClienteResponseDTO> listarTodosClientes() {
        return clientesRepository.findAll()
                .stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO dto) {
        String emailTratado = dto.email() != null ? dto.email().toLowerCase().trim() : "";

        // Remove tudo que não for número (pontos, traços, parênteses e espaços)
        String cpfTratado = dto.cpf() != null ? dto.cpf().replaceAll("\\D", "") : "";
        String telefoneTratado = dto.telefone() != null ? dto.telefone().replaceAll("\\D", "") : "";

        // Validações manuais rigorosas
        if (cpfTratado.length() != 11) {
            throw new RegraDeNegocioException("O CPF deve conter exatamente 11 números.");
        }
        if (telefoneTratado.length() != 11) {
            throw new RegraDeNegocioException("O telefone deve conter exatamente 11 números (DDD + 9 dígitos).");
        }

        // Validações de duplicidade no banco
        if (usuariosRepository.existsByEmail(emailTratado)) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }
        if (clientesRepository.existsByCpf(cpfTratado)) {
            throw new RegraDeNegocioException("CPF já cadastrado.");
        }
        if (clientesRepository.existsByTelefone(telefoneTratado)) {
            throw new RegraDeNegocioException("Telefone já cadastrado.");
        }

        // 1. Cria a entidade Usuário (Login/Autenticação)
        Usuarios novoUsuario = new Usuarios();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(emailTratado);

        String senha = dto.senha() != null && !dto.senha().isBlank() ? dto.senha() : "123456";
        novoUsuario.setSenha(passwordEncoder.encode(senha));
        novoUsuario.setRole("ROLE_CLIENTE");
        novoUsuario.setDataCriacao(LocalDateTime.now());

        // 2. Cria a entidade Cliente (Dados Pessoais) e vincula o Usuário
        Clientes novoCliente = new Clientes();
        novoCliente.setTelefone(telefoneTratado); // Salva limpo
        novoCliente.setCpf(cpfTratado); // Salva limpo
        novoCliente.setDataNascimento(dto.dataNascimento());
        novoCliente.setUsuario(novoUsuario);

        if (dto.endereco() != null) {
            novoCliente.setEnderecos(new Enderecos(dto.endereco()));
        }

        clientesRepository.save(novoCliente);
        return new ClienteResponseDTO(novoCliente);
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Clientes cliente = clientesRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));

        // Limpeza dos dados
        String cpfTratado = dto.cpf() != null ? dto.cpf().replaceAll("\\D", "") : "";
        String telefoneTratado = dto.telefone() != null ? dto.telefone().replaceAll("\\D", "") : "";

        // Validações manuais rigorosas
        if (cpfTratado.length() != 11) {
            throw new RegraDeNegocioException("O CPF deve conter exatamente 11 números.");
        }
        if (telefoneTratado.length() != 11) {
            throw new RegraDeNegocioException("O telefone deve conter exatamente 11 números (DDD + 9 dígitos).");
        }

        // Atualiza dados da entidade Cliente
        cliente.setTelefone(telefoneTratado);
        cliente.setCpf(cpfTratado);
        cliente.setDataNascimento(dto.dataNascimento());

        // Atualiza dados da entidade Usuário vinculada
        Usuarios usuarioVinculado = cliente.getUsuario();
        usuarioVinculado.setNome(dto.nome());

        String emailTratado = dto.email() != null ? dto.email().toLowerCase().trim() : "";
        if (!usuarioVinculado.getEmail().equals(emailTratado) && usuariosRepository.existsByEmail(emailTratado)) {
            throw new RegraDeNegocioException("E-mail já cadastrado para outro usuário.");
        }
        usuarioVinculado.setEmail(emailTratado);

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuarioVinculado.setSenha(passwordEncoder.encode(dto.senha()));
        }

        Clientes salvo = clientesRepository.save(cliente);
        return new ClienteResponseDTO(salvo);
    }
}