package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.domain.entity.Enderecos;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.EnderecoRepository;
import br.com.abadeus.infra.external.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private CepService cepService;

    public EnderecoResponseDTO listarEnderecoPorId(Long id) {
        return enderecoRepository.findById(id)
                .map(EnderecoResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Endereço não encontrado."));
    }

    public List<EnderecoResponseDTO> listarTodosEnderecos() {
        return enderecoRepository.findAll()
                .stream()
                .map(EnderecoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnderecoResponseDTO criarEndereco(EnderecoRequestDTO dto) {
        Enderecos novoEndereco = new Enderecos(dto);

        if (dto.logradouro() == null || dto.logradouro().isBlank()) {
            var dadosCep = cepService.consultarCep(dto.cep());

            if (dadosCep == null || dadosCep.erro() != null) {
                throw new RegraDeNegocioException("CEP não localizado ou inválido.");
            }

            novoEndereco.setLogradouro(dadosCep.logradouro());
            novoEndereco.setBairro(dadosCep.bairro());
            novoEndereco.setCidade(dadosCep.cidade());
            novoEndereco.setUf(dadosCep.uf());
        }

        Enderecos salvo = enderecoRepository.save(novoEndereco);
        return new EnderecoResponseDTO(salvo);
    }

    @Transactional
    public EnderecoResponseDTO atualizarEndereco(Long id, EnderecoRequestDTO dto) {
        Enderecos endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Endereço não encontrado."));

        endereco.setCep(dto.cep().replaceAll("\\D", ""));
        endereco.setNumero(dto.numero());
        endereco.setLogradouro(dto.logradouro());
        endereco.setComplemento(dto.complemento());
        endereco.setBairro(dto.bairro());
        endereco.setCidade(dto.cidade());
        endereco.setUf(dto.uf());

        Enderecos salvo = enderecoRepository.save(endereco);
        return new EnderecoResponseDTO(salvo);
    }
}