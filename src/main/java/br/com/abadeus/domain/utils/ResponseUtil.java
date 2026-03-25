package br.com.abadeus.domain.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static Map<String, Object> response(Object response) {
        Map<String, Object> retorno = new HashMap<>();
        retorno.put("mensagem", response);

        return retorno;
    }
}
