package br.com.abadeus.presentation.handler;

import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.utils.ResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> tratarErroDeConversaoJSON(HttpMessageNotReadableException ex) {
        String mensagemErro = ex.getMessage();

        // Verifica se o erro foi especificamente por causa da data (LocalDate)
        if (mensagemErro != null && mensagemErro.contains("java.time.LocalDate")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.response("Formato de data inválido.(Ex: 25/12/1995)"));
        }

        // Mensagem genérica para outros erros de formatação no JSON
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.response("Erro na leitura dos dados. Verifique se o JSON está no formato correto."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> tratarErro400(MethodArgumentNotValidException ex) {
        // Pega todos os erros de validação e junta numa string separada por vírgula
        String mensagens = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.response(mensagens));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<?> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(determinarStatusErro(ex.getMessage()))
                .body(ResponseUtil.response(ex.getMessage()));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public ResponseEntity<?> tratarErroAcessoNegado(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.response("Você precisa estar autenticado para acessar esse recurso."));
    }

    @ExceptionHandler({EntityNotFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<?> tratarErro404() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.response("Recurso não encontrado."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> tratarErroDeBanco(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseUtil.response("Erro de integridade no banco de dados. Este registro já existe ou está vinculado a outro."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> tratarErro500(Exception ex) {
        // Logar o erro no console para você saber o que quebrou no servidor
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.response("Erro interno no servidor. Tente novamente mais tarde."));
    }

    private HttpStatus determinarStatusErro(String mensagem) {
        if (mensagem == null) return HttpStatus.BAD_REQUEST;
        String msgLower = mensagem.toLowerCase();

        if (msgLower.contains("credenciais") || msgLower.contains("senha atual incorreta") ||
                msgLower.contains("token inválido") || msgLower.contains("precisa estar autenticado")) {
            return HttpStatus.UNAUTHORIZED; // 401
        }
        if (msgLower.contains("não encontrado")) {
            return HttpStatus.NOT_FOUND; // 404
        }
        if (msgLower.contains("permissão") || msgLower.contains("acesso negado")) {
            return HttpStatus.FORBIDDEN; // 403
        }
        if (msgLower.contains("já existe") || msgLower.contains("cadastrado")) {
            return HttpStatus.CONFLICT; // 409
        }
        return HttpStatus.BAD_REQUEST; // 400"
    }
}