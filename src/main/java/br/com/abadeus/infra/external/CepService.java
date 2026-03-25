package br.com.abadeus.infra.external;

import br.com.abadeus.application.dto.endereco.CepResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CepService {

    private final WebClient webClient;

    public CepService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://viacep.com.br/ws").build();
    }

    @CircuitBreaker(name = "viacep", fallbackMethod = "fallbackCep")
    public CepResponseDTO consultarCep(String cep) {
        String cepLimpo = cep.replaceAll("\\D", "");

        return webClient.get()
                .uri("/{cep}/json/", cepLimpo)
                .retrieve()
                .bodyToMono(CepResponseDTO.class)
                .block();
    }

    public CepResponseDTO fallbackCep(String cep, Throwable e) {
        System.err.println("ViaCEP indisponível ou erro. Acionando fallback: " + e.getMessage());
        return new CepResponseDTO(
                cep,
                "NA",
                "",
                "NA",
                "NA",
                "NA",
                true
        );
    }
}