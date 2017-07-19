package br.pucminas.pucmed.ui.extra;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;

@SuppressWarnings("serial")
public class CustomMessagesProvider implements SystemMessagesProvider {

	@Override
	public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
		CustomizedSystemMessages messages = new CustomizedSystemMessages();
		messages.setAuthenticationErrorCaption("Erro de autenticação");
		messages.setCommunicationErrorCaption("Servidor não encontrado");
		messages.setCookiesDisabledCaption("Cookies desabilitados");
		messages.setInternalErrorCaption("Erro interno do servidor");
		messages.setSessionExpiredCaption("Sessão expirada");
		return messages;
	}
}
