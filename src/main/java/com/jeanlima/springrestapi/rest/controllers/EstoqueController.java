package com.jeanlima.springrestapi.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jeanlima.springrestapi.model.Estoque;
import com.jeanlima.springrestapi.rest.dto.EstoqueDTO;
import com.jeanlima.springrestapi.service.EstoqueService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/estoques")
@RestController
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService service;

    @GetMapping("{id}")
    public Estoque getEstoqueById( @PathVariable Integer id ){
        return service.obterEstoque(id);
    }

    @GetMapping("/produto_id/{id}")
    public Estoque getEstoqueByProdutoId( @PathVariable Integer id ){
        return service.obterEstoquePorProdutoId(id);
    }

    @GetMapping("/descricao/{descricao}")
    public Estoque getEstoqueByDescricao( @PathVariable String descricao ){
        return service.obterEstoquePorDescricaoProduto(descricao);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estoque save( @RequestBody EstoqueDTO dto ){
        return service.salvar(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable Integer id ){
        service.deletar(id);
    }

    @PatchMapping("att/")
    public void atualizaCliente( @RequestBody EstoqueDTO clientDTO) {
        service.atualizaEstoque(clientDTO);
    }
}
