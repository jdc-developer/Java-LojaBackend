package jdc.loja.domain;

import java.util.Date;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import jdc.loja.domain.enums.EstadoPagamento;

@Entity
public class PagamentoBoleto extends Pagamento {

	private static final long serialVersionUID = 1L;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date dtVencimento;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date dtPagamento;

	public Date getDtVencimento() {
		return dtVencimento;
	}

	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}

	public Date getDtPagamento() {
		return dtPagamento;
	}

	public void setDtPagamento(Date dtPagamento) {
		this.dtPagamento = dtPagamento;
	}

	public PagamentoBoleto(Integer id, EstadoPagamento estado, Pedido pedido, Date dtVencimento, Date dtPagamento) {
		super(id, estado, pedido);
		this.dtVencimento = dtVencimento;
		this.dtPagamento = dtPagamento;
	}

	public PagamentoBoleto() {
		super();
	}
	
}
