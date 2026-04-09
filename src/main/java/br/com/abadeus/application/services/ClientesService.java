package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.cliente.ClienteRequestDTO;
import br.com.abadeus.application.dto.cliente.ClienteResponseDTO;
import br.com.abadeus.application.dto.cliente.ClienteUpdateDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelRequestDTO;
import br.com.abadeus.domain.entity.*;
import br.com.abadeus.domain.enums.TipoCliente;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.interfaces.IEnvioEmail;
import br.com.abadeus.domain.repository.ClientesRepository;
import br.com.abadeus.domain.repository.UsuariosRepository;
import br.com.abadeus.infra.external.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private IEnvioEmail envioEmail;

    @Value("${api.frontend.base-url}")
    private String baseUrlConfirmacao;

    public ClienteResponseDTO listarClientePorId(Long id) {
        return clientesRepository.findByIdAndDeletadoEmIsNull(id)
                .map(ClienteResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));
    }

    public List<ClienteResponseDTO> listarTodosClientes() {
        return clientesRepository.findAllByDeletadoEmIsNull()
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
        if (usuariosRepository.existsByEmail(emailTratado)) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }
        if (clientesRepository.existsByCpf(cpfTratado)) {
            throw new RegraDeNegocioException("CPF já cadastrado.");
        }
        if (clientesRepository.existsByTelefone(telefoneTratado)) {
            throw new RegraDeNegocioException("Telefone já cadastrado.");
        }

        String tokenConfirmacao = UUID.randomUUID().toString();

        Usuarios novoUsuario = new Usuarios();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(emailTratado);
        novoUsuario.setSenha(passwordEncoder.encode(
                (dto.senha() != null && !dto.senha().isBlank()) ? dto.senha() : "123456"
        ));
        novoUsuario.setRole("ROLE_CLIENTE");
        novoUsuario.setDataCriacao(LocalDateTime.now());
        novoUsuario.setAtivo(false);
        novoUsuario.setTokenAtivacao(tokenConfirmacao);

        Clientes novoCliente = new Clientes();
        novoCliente.setTelefone(telefoneTratado);
        novoCliente.setCpf(cpfTratado);
        novoCliente.setDataNascimento(dto.dataNascimento());
        novoCliente.setTipoCliente(dto.tipoCliente() != null ? dto.tipoCliente() : TipoCliente.NORMAL);
        novoCliente.setUsuario(novoUsuario);

        if (dto.endereco() != null) {
            novoCliente.setEndereco(resolverEndereco(dto.endereco()));
        }

        clientesRepository.save(novoCliente);

        envioEmail.enviarEmailTemplate(
                novoUsuario.getEmail(),
                "Confirme sua conta - Senac",
                baseUrlConfirmacao + "/auth/confirmar-email?token=" + tokenConfirmacao,
                "confirmar-email"
        );

        return new ClienteResponseDTO(novoCliente);
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateDTO dto) {
        Clientes cliente = clientesRepository.findByIdAndDeletadoEmIsNull(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));

        String telefoneTratado = dto.telefone().replaceAll("\\D", "");

        if (!cliente.getTelefone().equals(telefoneTratado) && clientesRepository.existsByTelefone(telefoneTratado)) {
            throw new RegraDeNegocioException("Telefone já cadastrado para outro cliente.");
        }

        cliente.setTelefone(telefoneTratado);

        if (!isBlank(dto.urlDocumento())) {
            cliente.setUrlDocumento(dto.urlDocumento());
        }

        if (dto.tipoCliente() != null) {
            cliente.setTipoCliente(dto.tipoCliente());
        }

        mergeResponsaveis(cliente, dto.responsaveis());
        mergeResponsaveisBuscar(cliente, dto.responsaveisBuscar());

        Usuarios usuario = cliente.getUsuario();
        usuario.setNome(dto.nome());

        if (!isBlank(dto.senha())) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (dto.endereco() != null) {
            cliente.setEndereco(resolverEndereco(dto.endereco()));
        }

        return new ClienteResponseDTO(clientesRepository.save(cliente));
    }

    @Transactional
    public void deletarCliente(Long id) {
        Clientes cliente = clientesRepository.findByIdAndDeletadoEmIsNull(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado."));

        cliente.setDeletadoEm(LocalDateTime.now());
        cliente.setAtivo(false);
        clientesRepository.save(cliente);
    }

    private void mergeResponsaveis(Clientes cliente, List<ResponsavelRequestDTO> dtos) {
        if (dtos == null) return;

        if (cliente.getResponsaveis() == null) {
            cliente.setResponsaveis(new ArrayList<>());
        }

        List<Long> idsEnviados = dtos.stream()
                .map(ResponsavelRequestDTO::id)
                .filter(java.util.Objects::nonNull)
                .toList();

        cliente.getResponsaveis().removeIf(r -> r.getId() != null && !idsEnviados.contains(r.getId()));

        for (ResponsavelRequestDTO dto : dtos) {
            if (dto.id() != null) {
                cliente.getResponsaveis().stream()
                        .filter(r -> r.getId().equals(dto.id()))
                        .findFirst()
                        .ifPresent(existente -> {
                            existente.setNome(dto.nome());
                            existente.setCpf(dto.cpf().replaceAll("\\D", ""));
                            existente.setTelefone(dto.telefone().replaceAll("\\D", ""));
                        });
            } else {
                Responsavel novo = new Responsavel();
                novo.setNome(dto.nome());
                novo.setCpf(dto.cpf().replaceAll("\\D", ""));
                novo.setTelefone(dto.telefone().replaceAll("\\D", ""));
                novo.setCliente(cliente);
                cliente.getResponsaveis().add(novo);
            }
        }
    }

    private void mergeResponsaveisBuscar(Clientes cliente, List<ResponsavelBuscarRequestDTO> dtos) {
        if (dtos == null) return;

        if (cliente.getResponsaveisBuscar() == null) {
            cliente.setResponsaveisBuscar(new ArrayList<>());
        }

        List<Long> idsEnviados = dtos.stream()
                .map(ResponsavelBuscarRequestDTO::id)
                .filter(java.util.Objects::nonNull)
                .toList();

        cliente.getResponsaveisBuscar().removeIf(r -> r.getId() != null && !idsEnviados.contains(r.getId()));

        for (ResponsavelBuscarRequestDTO dto : dtos) {
            if (dto.id() != null) {
                cliente.getResponsaveisBuscar().stream()
                        .filter(r -> r.getId().equals(dto.id()))
                        .findFirst()
                        .ifPresent(existente -> {
                            existente.setNome(dto.nome());
                            existente.setCpf(dto.cpf().replaceAll("\\D", ""));
                            existente.setTelefone(dto.telefone().replaceAll("\\D", ""));
                        });
            } else {
                ResponsavelBuscar novo = new ResponsavelBuscar();
                novo.setNome(dto.nome());
                novo.setCpf(dto.cpf().replaceAll("\\D", ""));
                novo.setTelefone(dto.telefone().replaceAll("\\D", ""));
                novo.setCliente(cliente);
                cliente.getResponsaveisBuscar().add(novo);
            }
        }
    }

    private Enderecos resolverEndereco(br.com.abadeus.application.dto.endereco.EnderecoRequestDTO dto) {
        Enderecos endereco = new Enderecos(dto);

        if (isBlank(dto.logradouro()) || isBlank(dto.bairro()) || isBlank(dto.cidade())) {
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