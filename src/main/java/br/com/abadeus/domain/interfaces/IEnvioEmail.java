package br.com.abadeus.domain.interfaces;

public interface IEnvioEmail {

    void enviarEmailTemplate (String para, String assunto, String link, String nomeTemplate);

}