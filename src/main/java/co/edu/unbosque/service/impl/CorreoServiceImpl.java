package co.edu.unbosque.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import co.edu.unbosque.service.api.CorreoServiceAPI;

@Service
public class CorreoServiceImpl implements CorreoServiceAPI {

	private static final Logger logger = LoggerFactory.getLogger(CorreoServiceImpl.class);

	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public void enviarCredenciales(String correoDestino, String username, String password) {
		try {
			SimpleMailMessage mensaje = new SimpleMailMessage();
			
			mensaje.setTo(correoDestino);
			mensaje.setSubject("Bienvenido al Sistema - Tus Credenciales de Ingreso");
			mensaje.setText("Hola " + username + ",\n\n" +
			                "Tu cuenta ha sido creada exitosamente en la plataforma.\n\n" +
			                "Tus credenciales de acceso son las siguientes:\n" +
			                "• Usuario: " + username + "\n" +
			                "• Contraseña: " + password + "\n\n" +
			                "Por motivos de seguridad, recuerda cambiar tu contraseña periódicamente.");
			
			mailSender.send(mensaje);
			
			logger.info("Correo enviado correctamente a: {}", correoDestino);
			
		} catch (Exception e) {
			logger.error("Error al enviar el correo a {}: {}", correoDestino, e.getMessage());
		}
	}
}