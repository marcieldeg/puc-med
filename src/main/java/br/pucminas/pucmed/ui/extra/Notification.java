package br.pucminas.pucmed.ui.extra;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.themes.ValoTheme;

public class Notification {
	public static void show(String message) {
		show(message, "Aviso", Type.INFORMATION);
	}

	public static void show(String message, Type type) {
		show(message, Type.INFORMATION.equals(type) ? "Aviso" : "Erro", type);
	}

	public static void show(String caption, String message, Type type) {
		com.vaadin.ui.Notification notification = new com.vaadin.ui.Notification(caption, message);
		notification.setStyleName(type.getValue() + " " + ValoTheme.NOTIFICATION_SMALL);
		notification.setDelayMsec(10000);
		notification.setPosition(Position.TOP_RIGHT);
		notification.show(Page.getCurrent());
	}

	public enum Type {
		INFORMATION(ValoTheme.NOTIFICATION_SUCCESS), //
		ERROR(ValoTheme.NOTIFICATION_FAILURE);

		String value;

		private Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
