package com.jeanlima.springrestapi.service.impl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.jeanlima.springrestapi.enums.StatusPedido;
import com.jeanlima.springrestapi.exception.PedidoNaoEncontradoException;
import com.jeanlima.springrestapi.exception.RegraNegocioException;
import com.jeanlima.springrestapi.model.Cliente;
import com.jeanlima.springrestapi.model.Estoque;
import com.jeanlima.springrestapi.model.ItemPedido;
import com.jeanlima.springrestapi.model.Pedido;
import com.jeanlima.springrestapi.model.Produto;
import com.jeanlima.springrestapi.repository.ClienteRepository;
import com.jeanlima.springrestapi.repository.ItemPedidoRepository;
import com.jeanlima.springrestapi.repository.PedidoRepository;
import com.jeanlima.springrestapi.repository.ProdutoRepository;
import com.jeanlima.springrestapi.rest.dto.EstoqueDTO;
import com.jeanlima.springrestapi.rest.dto.ItemPedidoDTO;
import com.jeanlima.springrestapi.rest.dto.PedidoDTO;
import com.jeanlima.springrestapi.service.EstoqueService;
import com.jeanlima.springrestapi.service.PedidoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clientesRepository;
    private final ProdutoRepository produtosRepository;
    private final ItemPedidoRepository itemsPedidoRepository;
    private final EstoqueService estoqueService;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();

        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);


        BigDecimal total = new BigDecimal(0);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());

        for (ItemPedido itemPedido : itemsPedido) {
            Estoque estoqueAtual = estoqueService.obterEstoquePorProdutoId(itemPedido.getPedido().getId());

            if(estoqueAtual.getQuantidade() < itemPedido.getQuantidade()){
                throw new RegraNegocioException("Quantidade do Produto "+ itemPedido.getProduto().getDescricao() + " Insuficiente no Estoque");
            }else{
                EstoqueDTO estoqueDTO = new EstoqueDTO();
                estoqueDTO.setId(estoqueAtual.getId());
                int numAtual = estoqueAtual.getQuantidade();
                estoqueDTO.setQuantidade(numAtual - itemPedido.getQuantidade());
                estoqueService.atualizaEstoque(estoqueDTO);


                //atualiza preço total
                BigDecimal d = new BigDecimal(itemPedido.getQuantidade());
                total = total.add(itemPedido.getProduto().getPreco().multiply(d));
            }
        }

        pedido.setTotal(total);

        pedidoRepository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }

    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items) {
        if (items.isEmpty()) {
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
                .stream()
                .map(dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: " + idProduto));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {

        return pedidoRepository.findByIdFetchItens(id);
    }

    @Override
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        pedidoRepository
                .findById(id)
                .map(pedido -> {
                    pedido.setStatus(statusPedido);
                    return pedidoRepository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException());

    }



    @Override
    public void deletar(Integer id) {
        pedidoRepository.findById(id)
                .map( pedido -> {
                    pedidoRepository.delete(pedido);
                    return pedido;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido não encontrado") );

    }

    @Override
    public void atualizaClientePedido(Integer id, Integer clienteId) {

        pedidoRepository
                .findById(id)
                .map(pedido -> {
                    pedido.setCliente(
                            clientesRepository.findById(clienteId)
                                    .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado")));
                    return pedidoRepository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException());
    }


}