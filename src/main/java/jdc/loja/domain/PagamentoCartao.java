package jdc.loja.domain;

import javax.persistence.Entity;

import jdc.loja.domain.enums.EstadoPagamento;

@Entity
public class PagamentoCartao extends Pagamento {

	private static final long serialVersionUID = 1L;
	private Integer numeroParcelas;

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

	public PagamentoCartao(Integer id, EstadoPagamento estado, Pedido pedido, Integer numeroParcelas) {
		super(id, estado, pedido);
		this.numeroParcelas = numeroParcelas;
	}

	public PagamentoCartao() {
		super();
	}
	
}
