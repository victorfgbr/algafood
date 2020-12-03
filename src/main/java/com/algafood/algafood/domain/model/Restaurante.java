package com.algafood.algafood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	
	private BigDecimal taxaFrete;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cozinha_id", nullable = false)
	private Cozinha cozinha;
	
	@ManyToMany
	@JoinTable(name = "restaurante_forma_pagamento", 
			joinColumns = @JoinColumn(name = "restaurante_id"),
			inverseJoinColumns =  @JoinColumn(name = "forma_pagamento_id"))
	private List<FormaPagamento> formasPagamento = new ArrayList<>(); 
	
	@Embedded
	private Endereco endereco;
	
	@Column(nullable = false, columnDefinition = "datetime")
	@CreationTimestamp
	private LocalDateTime dataCadastro;
	
	@Column(nullable = false, columnDefinition = "datetime")
	@UpdateTimestamp
	private LocalDateTime dataAtualizacao;
	
	@OneToMany (mappedBy = "restaurante")
	private List<Produto> produtos;
	
}

