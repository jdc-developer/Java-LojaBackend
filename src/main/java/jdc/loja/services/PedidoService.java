package jdc.loja.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jdc.loja.domain.ItemPedido;
import jdc.loja.domain.PagamentoBoleto;
import jdc.loja.domain.Pedido;
import jdc.loja.domain.enums.EstadoPagamento;
import jdc.loja.repositories.ItemPedidoRepository;
import jdc.loja.repositories.PagamentoRepository;
import jdc.loja.repositories.PedidoRepository;
import jdc.loja.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository rep;
	
	@Autowired
	private BoletoService bolService;
	
	@Autowired
	private PagamentoRepository pagtoRep;
	
	@Autowired
	private ProdutoService prodServ;
	
	@Autowired
	private ItemPedidoRepository itemPedRep;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = rep.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()
				));
	}
	
	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoBoleto) {
			PagamentoBoleto pagto = (PagamentoBoleto) obj.getPagamento();
			bolService.preencherPagamentoBoleto(pagto, obj.getInstante());
		}
		obj = rep.save(obj);
		pagtoRep.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setPreco(prodServ.find(ip.getProduto().getId()).getPreco());
			ip.setPedido(obj);
		}
		itemPedRep.saveAll(obj.getItens());
		return obj;
	}

}
