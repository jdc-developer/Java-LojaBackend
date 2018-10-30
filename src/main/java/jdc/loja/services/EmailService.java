package jdc.loja.services;

import org.springframework.mail.SimpleMailMessage;

import jdc.loja.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);

	void sendEmail(SimpleMailMessage msg);
}