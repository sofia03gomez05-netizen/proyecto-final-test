package co.edu.unbosque.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VerificacionContrasenaException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public VerificacionContrasenaException(String message) {
		super(message);
	}
}
