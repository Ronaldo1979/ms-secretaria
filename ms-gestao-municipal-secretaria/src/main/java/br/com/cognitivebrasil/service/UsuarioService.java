package br.com.cognitivebrasil.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.cognitivebrasil.model.Usuario;
import br.com.cognitivebrasil.repositories.UsuarioRepository;

@Service
public class UsuarioService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Optional<Usuario> buscarPorEmail(String email) {
		return Optional.ofNullable(usuarioRepository.findByEmail(email));
	}
	
}
