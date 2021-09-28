package br.com.cognitivebrasil.validate;

import java.util.ArrayList;
import java.util.List;
import br.com.cognitivebrasil.model.Secretaria;

public class ValidateSecretaria {

	private List<String> errors;

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	public List<String> valida(Secretaria obj) {
		
		errors = new ArrayList<String>();
		
		try {
			
			if(obj == null) {
				errors.add("Os dados da secretaria não podem ser nulos.");
				return this.errors;
			}else {
				if(obj.getSobInvestigacao() == null)
					errors.add("Campo sobInvestigacao inválido.");
				
				if(obj.getResponsavel() == null || obj.getResponsavel().isBlank())
					errors.add("Campo responsável inválido.");
					
				if(obj.getPasta() == null)
					errors.add("Campo pasta inválido.");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.errors;
	}
}
