package br.com.cognitivebrasil.controller;

import java.util.ArrayList;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.cognitivebrasil.security.JwtTokenUtil;
import br.com.cognitivebrasil.security.dto.JwtAuthenticationDto;
import br.com.cognitivebrasil.security.dto.TokenDto;
import br.com.cognitivebrasil.util.Response;
import br.com.cognitivebrasil.util.TokenActive;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/auth")
@Api(value = "API Gestão Municipal de Secretaria")
@CrossOrigin(origins = "*")
public class AuthenticationController {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
	private static final String TOKEN_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	/**
	* Gera e retorna um novo token JWT.
	*
	* @param authenticationDto
	* @param result
	* @return ResponseEntity<Response<TokenDto>>
	* @throws AuthenticationException
	*/
	
	@PostMapping
	@ApiOperation(value = "Gera Token JWT",
		notes = "Para gerar o token é necessário preencher o campo authenticationDto informando os dados conforme cadastrados Ex.: {'email':'ronaldo.fjv@gmail.com', 'senha':'teste'}")
	public ResponseEntity<Response<TokenDto>> gerarTokenJwt(@Validated @RequestBody JwtAuthenticationDto authenticationDto, BindingResult result) throws AuthenticationException {
	
		Response<TokenDto> response = new Response<TokenDto>();
	
		if (result.hasErrors()) {
			log.error("Erro validando dados: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		
		log.info("Gerando token para o email {}.", authenticationDto.getEmail());
		
		Authentication authentication = authenticationManager.authenticate(
	                                  new UsernamePasswordAuthenticationToken(
	                                		  authenticationDto.getEmail(), authenticationDto.getSenha()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getEmail());
		String token = jwtTokenUtil.obterToken(userDetails);
		
		TokenActive.TOKEN = token;
		
		System.out.println(">>> Secretaria TokenActive: " + TokenActive.TOKEN);

		response.setData(new TokenDto(token));
		return ResponseEntity.ok(response);
	}
	
	/**
	* Gera um novo token com uma nova data de expiração.
	*
	* @param request
	* @return ResponseEntity<Response<TokenDto>>
	*/
	@PostMapping(value = "/refresh")
	@ApiOperation(value = "Atualiza Token JWT",
		notes = "Atualização de Token de acesso expirado")
	public ResponseEntity<Response<TokenDto>> gerarRefreshTokenJwt(HttpServletRequest request) {
		log.info("Gerando refresh token JWT.");
	
		Response<TokenDto> response = new Response<TokenDto>();
		response.setErrors(new ArrayList<String>());
		Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
		
		if (token.isPresent() && token.get().startsWith(BEARER_PREFIX)) {
			token = Optional.of(token.get().substring(7));
		}
		
		if (!token.isPresent()) {
			response.getErrors().add("Token não informado.");
			
		}else if (!jwtTokenUtil.tokenValido(token.get())) {
			response.getErrors().add("Token inválido ou expirado.");
		}
		
		if (!response.getErrors().isEmpty()) {
			return ResponseEntity.badRequest().body(response);
		}
		
		String refreshedToken = jwtTokenUtil.refreshToken(token.get());
		response.setData(new TokenDto(refreshedToken));
		
		return ResponseEntity.ok(response);
	}


}
