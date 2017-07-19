package br.pucminas.pucmed.email;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

public class Email {
	public static boolean isValid(String email) {
		return new EmailValidator().isValid(email, null);
	}

	public static void enviarSenha(String email) {
		// TODO: algoritmo para enviar senha por email
	}
}
