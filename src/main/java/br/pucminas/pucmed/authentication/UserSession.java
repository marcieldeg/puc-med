package br.pucminas.pucmed.authentication;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSession {
	private String name;
	private String role;

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
