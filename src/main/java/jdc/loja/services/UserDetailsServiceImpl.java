package jdc.loja.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jdc.loja.domain.Cliente;
import jdc.loja.repositories.ClienteRepository;
import jdc.loja.security.UserSS;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private ClienteRepository rep;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Cliente> cliOpt = rep.findByEmail(email);
		if (!cliOpt.isPresent()) {
			throw new UsernameNotFoundException(email);
		}
		Cliente cli = cliOpt.get();
		return new UserSS(cli.getId(), cli.getEmail(), cli.getSenha(), cli.getPerfis());
	}

}
