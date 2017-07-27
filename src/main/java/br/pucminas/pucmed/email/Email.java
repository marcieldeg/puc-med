package br.pucminas.pucmed.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import br.pucminas.pucmed.bean.BeanConfiguration;
import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.service.UsuarioService;

public class Email {
	private static Log log = LogFactory.getLog(Email.class);

	public static boolean isValid(String email) {
		return new EmailValidator().isValid(email, null);
	}

	public static void enviarSenha(String address) {
		Usuario usuario = BeanGetter.getService(UsuarioService.class).getByEmail(address);
		if (usuario == null) {
			log.error("Usuário não encontrado para o email \"" + address + "\".");
			return;
		}
		
		BeanConfiguration config = BeanGetter.getBean(BeanConfiguration.class);

		new Thread() {
			@Override
			public void run() {
				try {
					SimpleEmail email = new SimpleEmail();
					email.setHostName(config.getEmailHostName());
					email.addTo(address, usuario.getNome());
					email.setFrom(config.getEmailAddress(), "PUC-MED");
					email.setSmtpPort(config.getEmailPort());
					email.setAuthenticator(new DefaultAuthenticator(config.getEmailUsername(), config.getEmailPassword()));
					email.setSSLOnConnect(true);
					email.setSubject("PUC-MED - Recuperação de senha");
					email.setMsg("Login: " + usuario.getLogin() + ", Senha: " + usuario.getSenha());
					email.send();
					log.info("E-mail enviado para \"" + address + "\".");
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
			}
		}.start();
	}
}
