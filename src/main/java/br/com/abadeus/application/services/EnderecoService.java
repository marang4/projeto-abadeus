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
    public Enderecos criarEndereco(EnderecoRequestDTO dto) {
        Enderecos endereco = new Enderecos(dto);

        boolean dadosFaltando = isBlank(dto.logradouro()) || isBlank(dto.bairro()) || isBlank(dto.cidade()) || isBlank(dto.uf());

        if (dadosFaltando) {
            var dadosCep = cepService.consultarCep(dto.cep());

            if (dadosCep == null || Boolean.TRUE.equals(dadosCep.erro())) {
                throw new RegraDeNegocioException("CEP não localizado. Informe logradouro, bairro, cidade e UF manualmente.");
            }

            if (isBlank(endereco.getLogradouro())) endereco.setLogradouro(dadosCep.logradouro());
            if (isBlank(endereco.getBairro()))     endereco.setBairro(dadosCep.bairro());
            if (isBlank(endereco.getCidade()))     endereco.setCidade(dadosCep.cidade());
            if (isBlank(endereco.getUf()))         endereco.setUf(dadosCep.uf());
        }

        return enderecoRepository.save(endereco);
    }

    @Transactional
    public EnderecoResponseDTO atualizarEndereco(Long id, EnderecoRequestDTO dto) {
        Enderecos endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Endereço não encontrado."));

        endereco.setCep(dto.cep().replaceAll("\\D", ""));
        endereco.setNumero(dto.numero());
        endereco.setComplemento(dto.complemento());

        boolean dadosFaltando = isBlank(dto.logradouro()) || isBlank(dto.bairro()) || isBlank(dto.cidade()) || isBlank(dto.uf());

        if (dadosFaltando) {
            var dadosCep = cepService.consultarCep(dto.cep());
            if (dadosCep == null || Boolean.TRUE.equals(dadosCep.erro())) {
                throw new RegraDeNegocioException("CEP não localizado. Informe logradouro, bairro, cidade e UF manualmente.");
            }
            if (isBlank(endereco.getLogradouro())) endereco.setLogradouro(dadosCep.logradouro());
            if (isBlank(endereco.getBairro()))     endereco.setBairro(dadosCep.bairro());
            if (isBlank(endereco.getCidade()))     endereco.setCidade(dadosCep.cidade());
            if (isBlank(endereco.getUf()))         endereco.setUf(dadosCep.uf());
        } else {
            endereco.setLogradouro(dto.logradouro());
            endereco.setBairro(dto.bairro());
            endereco.setCidade(dto.cidade());
            endereco.setUf(dto.uf());
        }

        return new EnderecoResponseDTO(enderecoRepository.save(endereco));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}