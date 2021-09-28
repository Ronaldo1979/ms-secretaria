package br.com.cognitivebrasil.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import br.com.cognitivebrasil.model.Secretaria;
import br.com.cognitivebrasil.model.Usuario;
import br.com.cognitivebrasil.service.SecretariaService;
import br.com.cognitivebrasil.util.Response;
import br.com.cognitivebrasil.validate.ValidateSecretaria;
import br.com.cognitivebrasil.validate.ValidateUsuario;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/resource")
@Api(value = "API Gestão Municipal de Secretaria")
@CrossOrigin(origins = "*")
public class SecretariaAPI {
	
	@Autowired
	SecretariaService secretariaService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping(value = "secretariats")
	@ApiOperation(value = "Cadastra Secretaria",
		notes = "Cadastra uma nova secretaria")
	public ResponseEntity<Response<Secretaria>> cadastraSecretaria(@Validated @RequestBody String obj){
		
		System.out.println("***********************************");
		System.out.println(">>> Cadastrando Secretaria...");
		System.out.println("***********************************");
		
		Secretaria secretaria = null;
		Response<Secretaria> response = new Response<Secretaria>();
		ValidateSecretaria validate = new ValidateSecretaria();
		List<String> listaErros = new ArrayList<String>();
		
		try {
			
			Gson gson = new Gson();
			JsonObject jsonObject = JsonParser.parseString(obj).getAsJsonObject();
			
			secretaria = gson.fromJson(jsonObject.toString(), Secretaria.class);
			
			if(secretaria != null) {
				
				List<String> erros = validate.valida(secretaria);
				
				if(erros == null || erros.isEmpty()) {
					
					Optional<Secretaria> secretariaCadastrada = secretariaService.buscaSecretaria(secretaria.getPasta());
					
					if(secretariaCadastrada.isEmpty() || secretariaCadastrada ==null) {
						
						secretaria = secretariaService.cadastraSecretaria(secretaria);
						response.setData(secretaria);
						
					}else {
						
						erros.add("Secretaria ("+secretariaCadastrada.get().getPasta().name()+") já cadastrada");
						response.setErrors(erros);
						
						secretaria.setId(secretariaCadastrada.get().getId());
						response.setData(secretaria);
						
						return ResponseEntity.badRequest().body(response);
					}
					
				}else {
					
					response.setErrors(erros);
					response.setData(secretaria);
					
					return ResponseEntity.badRequest().body(response);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
			listaErros.add("Erro ao cadastrar secretaria");
			response.setErrors(listaErros);
			
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping(value = "secretariats")
	@ApiOperation(value = "Lista Secretaria",
		notes = "Lista todas as secretarias cadastradas")
	public  List<Secretaria> listaSecretaria() {
		
		List<Secretaria> secretarias = null;
		
		try {
			secretarias = secretariaService.listaSecretaria();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return secretarias;
	}
	
	@GetMapping(value = "secretariats/{secretariaId}")
	@ApiOperation(value = "Busca Secretaria",
		notes = "Busca uma secretaria especifica de acordo com o ID")
	public ResponseEntity<Response<Secretaria>> buscaSecretaria(@PathVariable("secretariaId") Long secretariaId) {
		
		Response<Secretaria> response = new Response<Secretaria>();
		List<String> listaErros = new ArrayList<String>();
		Optional<Secretaria> secretaria = null;
		
		try {
		
			secretaria = secretariaService.buscaSecretaria(secretariaId);
			if(secretaria != null && !secretaria.isEmpty()) {
				response.setData(secretaria.get());
				return ResponseEntity.ok(response);
			
			}else {
				listaErros.add("Secretaria não encontrada");
				response.setErrors(listaErros);
				return ResponseEntity.badRequest().body(response);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			
			listaErros.add("Erro ao buscar secretaria");
			response.setErrors(listaErros);
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@PatchMapping(value = "secretariats/{secretariaId}")
	@ApiOperation(value = "Atualiza Status Secretaria",
		notes = "Atualiza o status da secretaria referente a estar sob investigação")
	public ResponseEntity<Response<Secretaria>> atualizaStatusSecretaria(@PathVariable("secretariaId") Long secretariaId){
		
		System.out.println("****************************************");
		System.out.println(">>> Alterando status sob_investigacao...");
		System.out.println("****************************************");
		
		Secretaria secretaria = null;
		Response<Secretaria> response = new Response<Secretaria>();
		List<String> listaErros = new ArrayList<String>();
		
		try {
			
			secretaria = secretariaService.buscaSecretaria(secretariaId).orElse(null);
			if(secretaria != null) {
				
				if(secretaria.getSobInvestigacao() == true)
					secretaria.setSobInvestigacao(false);
				else
					secretaria.setSobInvestigacao(true);
				
				secretaria = secretariaService.atualizaStatusSecretaria(secretaria);
				response.setData(secretaria);
			
			}else {
				
				listaErros.add("Secretaría não encontrada.");
				response.setErrors(listaErros);
				return ResponseEntity.badRequest().body(response);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
			listaErros.add("Erro ao atualizar status e investigação.");
			response.setErrors(listaErros);
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	//USER==================================================================//
	
	@PostMapping(value = "user")
	@ApiOperation(value = "Cadastra Usuário",
		notes = "Cadastra um novo usuário")
	public ResponseEntity<Response<Usuario>> cadastraUsuario(@Validated @RequestBody String obj){
		
		System.out.println("***********************************");
		System.out.println(">>> Cadastrando Usuario...");
		System.out.println("***********************************");
		
		Usuario usuario = null;
		Response<Usuario> response = new Response<Usuario>();
		ValidateUsuario validate = new ValidateUsuario();
		List<String> listaErros = new ArrayList<String>();
		
		try {
			
			Gson gson = new Gson();
			JsonObject jsonObject = JsonParser.parseString(obj).getAsJsonObject();
			
			usuario = gson.fromJson(jsonObject.toString(), Usuario.class);
			
			if(usuario != null) {
				
				List<String> erros = validate.valida(usuario);
				
				if(erros == null || erros.isEmpty()) {
					
					Optional<Usuario> userCadastrado = secretariaService.buscaUsuario(usuario.getEmail());
					
					if(userCadastrado.isEmpty() || userCadastrado == null) {
						usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
						usuario = secretariaService.cadastraUsuario(usuario);
						response.setData(usuario);
					}else {
						
						erros.add("Usuário " + usuario.getEmail() + " já cadastrado!");
						response.setErrors(erros);
						response.setData(usuario);
						return ResponseEntity.badRequest().body(response);
					}
					
				}else {
					
					response.setErrors(erros);
					response.setData(usuario);
					
					return ResponseEntity.badRequest().body(response);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
			listaErros.add("Erro ao cadastrar usuario");
			response.setErrors(listaErros);
			
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value = "user")
	@ApiOperation(value = "Lista Usuário",
		notes = "Lista todos os usuários")
	public  List<Usuario> listaUsuario() {
		
		List<Usuario> usuario = null;
		
		try {
			usuario = secretariaService.listaUsuario();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return usuario;
	}
	
	@GetMapping(value = "user/{email}")
	@ApiOperation(value = "Busca Usuário",
		notes = "Busca usuário por email")
	public ResponseEntity<Response<Usuario>> buscaUsuario(@PathVariable("email") String email) {
		
		Response<Usuario> response = new Response<Usuario>();
		List<String> listaErros = new ArrayList<String>();
		Optional<Usuario> usuario = null;
		
		try {
		
			usuario = secretariaService.buscaUsuario(email);
			if(usuario != null && !usuario.isEmpty()) {
				response.setData(usuario.get());
				return ResponseEntity.ok(response);
			
			}else {
				listaErros.add("Usuario não encontrado");
				response.setErrors(listaErros);
				return ResponseEntity.badRequest().body(response);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			
			listaErros.add("Erro ao buscar secretaria");
			response.setErrors(listaErros);
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@PutMapping(value = "user")
	@ApiOperation(value = "Atualiza Usuário",
		notes = "Atualiza senha do usuário")
	public ResponseEntity<Response<Usuario>> atualizaUsuario(@Validated @RequestBody String obj){
		
		System.out.println("***********************************");
		System.out.println(">>> Atualizando Usuario...");
		System.out.println("***********************************");
		
		Usuario usuario = null;
		Response<Usuario> response = new Response<Usuario>();
		ValidateUsuario validate = new ValidateUsuario();
		List<String> listaErros = new ArrayList<String>();
		
		try {
			
			Gson gson = new Gson();
			JsonObject jsonObject = JsonParser.parseString(obj).getAsJsonObject();
			
			usuario = gson.fromJson(jsonObject.toString(), Usuario.class);
			
			if(usuario != null) {
				
				List<String> erros = validate.valida(usuario);
				
				if(erros == null || erros.isEmpty()) {
					
					Optional<Usuario> userCadastrado = secretariaService.buscaUsuario(usuario.getEmail());
					
					if( userCadastrado != null && !userCadastrado.isEmpty()) {
						if(usuario.getId() == null)
							usuario.setId(userCadastrado.get().getId());
						usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
						usuario = secretariaService.atualizaUsuario(usuario);
						response.setData(usuario);
					}else {
						
						erros.add("Usuário " + usuario.getEmail() + " não existe!");
						response.setErrors(erros);
						response.setData(usuario);
						return ResponseEntity.badRequest().body(response);
					}
					
				}else {
					
					response.setErrors(erros);
					response.setData(usuario);
					
					return ResponseEntity.badRequest().body(response);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
			listaErros.add("Erro ao cadastrar usuario");
			response.setErrors(listaErros);
			
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	
}
