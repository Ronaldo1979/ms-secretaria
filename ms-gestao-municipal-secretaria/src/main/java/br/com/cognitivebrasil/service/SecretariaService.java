package br.com.cognitivebrasil.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.cognitivebrasil.enumerator.Pasta;
import br.com.cognitivebrasil.model.Secretaria;
import br.com.cognitivebrasil.model.Usuario;
import br.com.cognitivebrasil.repositories.SecretariaRepository;
import br.com.cognitivebrasil.repositories.UsuarioRepository;

@Service
public class SecretariaService {

	@Autowired
	private SecretariaRepository secretariaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Secretaria cadastraSecretaria(Secretaria obj) {
		Secretaria secretaria = null;
		try {
			secretaria = secretariaRepository.save(obj);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			secretariaRepository.flush();
		}
		return secretaria;
	}
	
	public List<Secretaria> listaSecretaria() {
		List<Secretaria> listaSecretaria = null;
		try {
			listaSecretaria = secretariaRepository.findAll();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			secretariaRepository.flush();
		}
		return listaSecretaria;
	}
	
	public Optional<Secretaria> buscaSecretaria(Pasta pasta) {
		
		Optional<Secretaria> secretaria = null;
		
		try {
			secretaria = Optional.ofNullable(secretariaRepository.findByPasta(pasta));
		}catch (NoSuchElementException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			secretariaRepository.flush();
		}
		return secretaria;
	}
	
	public Optional<Secretaria> buscaSecretaria(Long objId) {
		
		Optional<Secretaria> secretaria = null;
		
		try {
			secretaria = secretariaRepository.findById(objId);
		}catch (NoSuchElementException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			secretariaRepository.flush();
		}
		return secretaria;
	}
	
	public Secretaria atualizaStatusSecretaria(Secretaria obj) {
		Secretaria secretaria = null;
		try {
			secretaria = secretariaRepository.saveAndFlush(obj);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			secretariaRepository.flush();
		}
		return secretaria;
	}
	
	//USER==================================================================//
	
	public Usuario cadastraUsuario(Usuario obj) {
		Usuario usuario = null;
		try {
			usuario = usuarioRepository.save(obj);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			usuarioRepository.flush();
		}
		return usuario;
	}
	
	public Usuario atualizaUsuario(Usuario obj) {
		Usuario usuario = null;
		try {
			usuario = usuarioRepository.saveAndFlush(obj);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			usuarioRepository.flush();
		}
		return usuario;
	}
	
	public Optional<Usuario> buscaUsuario(String email) {
		
		Optional<Usuario> usuario = null;
		
		try {
			usuario = Optional.ofNullable(usuarioRepository.findByEmail(email));
		}catch (NoSuchElementException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			usuarioRepository.flush();
		}
		return usuario;
	}
	
	public List<Usuario> listaUsuario() {
		List<Usuario> listaUsuario = null;
		try {
			listaUsuario = usuarioRepository.findAll();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			usuarioRepository.flush();
		}
		return listaUsuario;
	}
	
	
}
