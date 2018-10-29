package jdc.loja.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import jdc.loja.domain.PagamentoBoleto;

@Service
public class BoletoService {

	public void preencherPagamentoBoleto(PagamentoBoleto pagto, Date instantePedido) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(instantePedido);
		cal.add(Calendar.DAY_OF_MONTH, 7);
		pagto.setDtVencimento(cal.getTime());
	}
}
