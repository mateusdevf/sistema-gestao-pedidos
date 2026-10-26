package com.weverson.gestao_pedidos;

import java.time.LocalDateTime;
import java.util.Map;

public class ErroResposta {
    private final LocalDateTime timestamp;
    private final int status;
    private final String mensagem;
    private final Map<String, String> erros;

    public ErroResposta(int status, String mensagem, Map<String, String> erros) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Map<String, String> getErros() {
        return erros;
    }
}
