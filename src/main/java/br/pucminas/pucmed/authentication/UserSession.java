package br.pucminas.pucmed.authentication;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;

import br.pucminas.pucmed.model.Laboratorio;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Recepcionista;
import br.pucminas.pucmed.model.Usuario;

public class UserSession {
	private Usuario usuario;

	public UserSession(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public boolean isMedicoRole() {
		return !(usuario instanceof Recepcionista) && !(usuario instanceof Laboratorio);
	}

	public boolean isRecepcionistaRole() {
		return !(usuario instanceof Medico) && !(usuario instanceof Laboratorio);
	}

	public boolean isLaboratorioRole() {
		return !(usuario instanceof Medico) && !(usuario instanceof Recepcionista);
	}

	public boolean isAdminRole() {
		return !isMedicoRole() && !isRecepcionistaRole() && !isLaboratorioRole();
	}

	public static final String CURRENT_USER = "user";

	private static WrappedSession getCurrentHttpSession() {
		VaadinSession s = VaadinSession.getCurrent();
		if (s == null)
			throw new IllegalStateException("Sessão não encontrada");

		return s.getSession();
	}

	public static UserSession get() {
		return (UserSession) getCurrentHttpSession().getAttribute(CURRENT_USER);
	}

	public static void set(UserSession userSession) {
		if (userSession == null) {
			getCurrentHttpSession().removeAttribute(CURRENT_USER);
		} else {
			getCurrentHttpSession().setAttribute(CURRENT_USER, userSession);
		}
	}

	public static boolean exists() {
		return getCurrentHttpSession().getAttribute(CURRENT_USER) != null;
	}
}
