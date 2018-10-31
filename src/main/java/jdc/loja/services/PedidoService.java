package jdc.loja.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jdc.loja.domain.Cliente;
import jdc.loja.domain.ItemPedido;
import jdc.loja.domain.PagamentoBoleto;
import jdc.loja.domain.Pedido;
import jdc.loja.domain.enums.EstadoPagamento;
import jdc.loja.repositories.ItemPedidoRepository;
import jdc.loja.repositories.PagamentoRepository;
import jdc.loja.repositories.PedidoRepository;
import jdc.loja.security.UserSS;
import jdc.loja.services.exceptions.AuthorizationException;
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
	
	@Autowired
	private ClienteService cliServ;
	
	@Autowired
	private EmailService emailServ;
	
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
		obj.setCliente(cliServ.find(obj.getCliente().getId()));
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
			ip.setProduto(prodServ.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedRep.saveAll(obj.getItens());
		emailServ.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageReq = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente = cliServ.find(user.getId());
		return rep.findByCliente(cliente, pageReq);
	}

}
