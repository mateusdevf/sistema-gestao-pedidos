package com.weverson.gestao_pedidos;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido criar(Pedido pedido) {
        pedido.setId(null);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public boolean deletar(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Pedido> atualizar(Long id, Pedido dadosNovos) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setCliente(dadosNovos.getCliente());
            pedido.setValorTotal(dadosNovos.getValorTotal());
            return pedidoRepository.save(pedido);
        });
    }
}
