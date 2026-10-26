package com.weverson.gestao_pedidos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void limparPedidos() {
        pedidoRepository.deleteAll();
    }

    @Test
    void deveCriarPedido() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente":"Mateus","valorTotal":125.50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.cliente").value("Mateus"))
                .andExpect(jsonPath("$.valorTotal").value(125.50));
        assertThat(pedidoRepository.count()).isEqualTo(1);
    }

    @Test
    void deveListarPedidos() throws Exception {
        salvarPedido();
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cliente").value("Mateus"));
    }

    @Test
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deveBuscarPedidoPorId() throws Exception {
        Pedido pedido = salvarPedido();
        mockMvc.perform(get("/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedido.getId()))
                .andExpect(jsonPath("$.cliente").value("Mateus"));
    }

    @Test
    void deveAtualizarPedido() throws Exception {
        Pedido pedido = salvarPedido();
        mockMvc.perform(put("/pedidos/{id}", pedido.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente":"Ana","valorTotal":200.75}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedido.getId()))
                .andExpect(jsonPath("$.cliente").value("Ana"))
                .andExpect(jsonPath("$.valorTotal").value(200.75));
        Pedido atualizado = pedidoRepository.findById(pedido.getId()).orElseThrow();
        assertThat(atualizado.getCliente()).isEqualTo("Ana");
        assertThat(atualizado.getValorTotal()).isEqualByComparingTo("200.75");
    }

    @Test
    void deveDeletarPedido() throws Exception {
        Pedido pedido = salvarPedido();
        mockMvc.perform(delete("/pedidos/{id}", pedido.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(pedidoRepository.existsById(pedido.getId())).isFalse();
    }

    @Test
    void deveRetornar404ParaPedidoInexistente() throws Exception {
        mockMvc.perform(get("/pedidos/999999")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/pedidos/999999")).andExpect(status().isNotFound());
        mockMvc.perform(put("/pedidos/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente":"Ana","valorTotal":100}
                                """))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void deveRejeitarClienteEmBranco(String cliente) throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cliente\":\"" + cliente + "\",\"valorTotal\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação nos campos informados"))
                .andExpect(jsonPath("$.erros.cliente").value("O nome do cliente é obrigatório"));
        assertThat(pedidoRepository.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-10", "null"})
    void deveRejeitarValorInvalido(String valor) throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cliente\":\"Mateus\",\"valorTotal\":" + valor + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.valorTotal").exists());
        assertThat(pedidoRepository.count()).isZero();
    }

    @Test
    void deveValidarAtualizacaoSemAlterarPedido() throws Exception {
        Pedido pedido = salvarPedido();
        mockMvc.perform(put("/pedidos/{id}", pedido.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente":"","valorTotal":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.cliente").exists())
                .andExpect(jsonPath("$.erros.valorTotal").exists());
        assertThat(pedidoRepository.findById(pedido.getId()).orElseThrow().getCliente())
                .isEqualTo("Mateus");
    }

    @Test
    void deveIgnorarIdInformadoNaCriacao() throws Exception {
        Pedido pedido = salvarPedido();
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + pedido.getId() + ",\"cliente\":\"Ana\",\"valorTotal\":50}"))
                .andExpect(status().isCreated());
        assertThat(pedidoRepository.count()).isEqualTo(2);
        assertThat(pedidoRepository.findById(pedido.getId()).orElseThrow().getCliente())
                .isEqualTo("Mateus");
    }

    @Test
    void deveDisponibilizarDocumentacao() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/pedidos'].get").exists())
                .andExpect(jsonPath("$.paths['/pedidos'].post").exists())
                .andExpect(jsonPath("$.paths['/pedidos/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/pedidos/{id}'].delete").exists());
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    private Pedido salvarPedido() {
        return pedidoRepository.save(new Pedido("Mateus", new BigDecimal("125.50")));
    }
}
