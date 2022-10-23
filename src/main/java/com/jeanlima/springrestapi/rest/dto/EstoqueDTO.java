package com.jeanlima.springrestapi.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EstoqueDTO {
    private Integer id;
    private Integer quantidade;
    private Integer produtoId;
}