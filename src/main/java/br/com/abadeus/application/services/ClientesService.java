package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.cliente.ClienteRequestDTO;
import br.com.abadeus.application.dto.cliente.ClienteResponseDTO;
import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.entity.Enderecos;
import br.com.abadeus.domain.entity.Usuarios;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.ClientesRepository;
import br.com.abadeus.domain.repository.UsuariosRepository;
import br.com.abadeus.infra.external.CepService;
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

    @Autowired
    private CepService cepService;

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
        String emailTratado    = dto.email().toLowerCase().trim();
        String cpfTratado      = dto.cpf().replaceAll("\\D", "");
        String telefoneTratado = dto.telefone().replaceAll("\\D", "");

        if (cpfTratado.length() != 11) {
            throw new RegraDeNegocioException("O CPF deve conter exatamente 11 números.");
        }
        if (telefoneTratado.length() < 10 || telefoneTratado.length() > 11) {
            throw new RegraDeNegocioException("O telefone deve conter 10 ou 11 dígitos (DDD + número).");
        }

        if (usuariosRepository.existsByEmail(emailTratado)) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }
        if (clientesRepository.existsByCpf(cpfTratado)) {
            throw new RegraDeNegocioException("CPF já cadastrado.");
        }
        if (clientesRepository.existsByTelefone(telefoneTratado)) {
            throw new RegraDeNegocioException("Telefone já cadastrado.");
        }

        Usuarios novoUsuario = new Usuarios();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(emailTratado);
        String senha = (dto.senha() != null && !dto.senha().isBlank()) ? dto.senha() : "123456";
        novoUsuario.setSenha(passwordEncoder.encode(senha));
        novoUsuario.setRole("ROLE_CLIENTE");
        novoUsuario.setDataCriacao(LocalDateTime.now());


        Clientes novoCliente = new Clientes();
        novoCliente.setTelefone(telefoneTratado);
        novoCliente.setCpf(cpfTratado);
        novoCliente.setDataNascimento(dto.dataNascimento());
        novoCliente.setUsuario(novoUsuario);


        if (dto.endereco() != null) {
            Enderecos endereco = resolverEndereco(dto.endereco());
            novoCliente.setEndereco(endereco);
        }

        clientesRepository.save(novoCliente);
        return new ClienteResponseDTO(novoCliente);
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Clientes cliente = clientesRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));

        String cpfTratado      = dto.cpf().replaceAll("\\D", "");
        String telefoneTratado = dto.telefone().replaceAll("\\D", "");

        if (cpfTratado.length() != 11) {
            throw new RegraDeNegocioException("O CPF deve conter exatamente 11 números.");
        }
        if (telefoneTratado.length() < 10 || telefoneTratado.length() > 11) {
            throw new RegraDeNegocioException("O telefone deve conter 10 ou 11 dígitos.");
        }

        if (!cliente.getCpf().equals(cpfTratado) && clientesRepository.existsByCpf(cpfTratado)) {
            throw new RegraDeNegocioException("CPF já cadastrado para outro cliente.");
        }
        if (!cliente.getTelefone().equals(telefoneTratado) && clientesRepository.existsByTelefone(telefoneTratado)) {
            throw new RegraDeNegocioException("Telefone já cadastrado para outro cliente.");
        }

        cliente.setTelefone(telefoneTratado);
        cliente.setCpf(cpfTratado);
        cliente.setDataNascimento(dto.dataNascimento());

        Usuarios usuarioVinculado = cliente.getUsuario();
        usuarioVinculado.setNome(dto.nome());

        String emailTratado = dto.email().toLowerCase().trim();
        if (!usuarioVinculado.getEmail().equals(emailTratado) && usuariosRepository.existsByEmail(emailTratado)) {
            throw new RegraDeNegocioException("E-mail já cadastrado para outro usuário.");
        }
        usuarioVinculado.setEmail(emailTratado);

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuarioVinculado.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (dto.endereco() != null) {
            Enderecos endereco = resolverEndereco(dto.endereco());
            cliente.setEndereco(endereco);
        }

        return new ClienteResponseDTO(clientesRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO adicionarEndereco(UsuarioPrincipalDTO autenticacao, Enderecos endereco) {
        Clientes cliente = clientesRepository.findByUsuarioEmail(autenticacao.email())
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado para o usuário logado."));

        cliente.setEndereco(endereco);
        return new ClienteResponseDTO(clientesRepository.save(cliente));
    }
    private Enderecos resolverEndereco(br.com.abadeus.application.dto.endereco.EnderecoRequestDTO dto) {
        Enderecos endereco = new Enderecos(dto);

        boolean dadosFaltando = isBlank(dto.logradouro()) || isBlank(dto.bairro()) || isBlank(dto.cidade());

        if (dadosFaltando) {
            var dadosCep = cepService.consultarCep(dto.cep());

            if (dadosCep == null || Boolean.TRUE.equals(dadosCep.erro())) {
                throw new RegraDeNegocioException(
                        "CEP não localizado. Informe logradouro, bairro e cidade manualmente."
                );
            }

            if (isBlank(endereco.getLogradouro())) endereco.setLogradouro(dadosCep.logradouro());
            if (isBlank(endereco.getBairro()))     endereco.setBairro(dadosCep.bairro());
            if (isBlank(endereco.getCidade()))     endereco.setCidade(dadosCep.cidade());
            if (isBlank(endereco.getUf()))         endereco.setUf(dadosCep.uf());
        }

        return endereco;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}