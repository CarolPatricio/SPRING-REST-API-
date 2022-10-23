package com.jeanlima.springrestapi.service;


import com.jeanlima.springrestapi.model.Estoque;
import com.jeanlima.springrestapi.rest.dto.EstoqueDTO;

public interface EstoqueService {
    Estoque salvar( EstoqueDTO dto );
    Estoque obterEstoquePorProdutoId(Integer produtoId);
    Estoque obterEstoquePorDescricaoProduto(String descricao);
    Estoque obterEstoque(Integer id);
    void atualizaEstoque(EstoqueDTO dto);
    void deletar(Integer id);
}