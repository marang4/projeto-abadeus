package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.domain.entity.Enderecos;
import br.com.abadeus.domain.repository.EnderecoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public EnderecoResponseDTO listarPorId(Long id) {
        return enderecoRepository.findById(id)
                .map(EnderecoResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    public List<EnderecoResponseDTO> listarTodosEnderecos() {
        return enderecoRepository.findAll()
                .stream()
                .map(EnderecoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public EnderecoResponseDTO criarEndereco(EnderecoRequestDTO enderecoRequestDTO) {
        Enderecos novoEndereco = new Enderecos();

        novoEndereco.setCep(enderecoRequestDTO.cep());
        novoEndereco.setNumero(enderecoRequestDTO.numero());
        novoEndereco.setLogradouro(enderecoRequestDTO.logradouro());
        novoEndereco.setComplemento(enderecoRequestDTO.complemento());
        novoEndereco.setBairro(enderecoRequestDTO.bairro());
        novoEndereco.setCidade(enderecoRequestDTO.cidade());
        novoEndereco.setUf(enderecoRequestDTO.uf());

        enderecoRepository.save(novoEndereco);

        return novoEndereco.toDtoResponse();
    }

    @Transactional
    public EnderecoResponseDTO atualizarEndereco(Long id, EnderecoRequestDTO enderecoRequestDTO) {
        Enderecos endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        endereco.setCep(enderecoRequestDTO.cep());
        endereco.setNumero(enderecoRequestDTO.numero());
        endereco.setLogradouro(enderecoRequestDTO.logradouro());
        endereco.setComplemento(enderecoRequestDTO.complemento());
        endereco.setBairro(enderecoRequestDTO.bairro());
        endereco.setCidade(enderecoRequestDTO.cidade());
        endereco.setUf(enderecoRequestDTO.uf());

        enderecoRepository.save(endereco);

        return endereco.toDtoResponse();
    }
}
