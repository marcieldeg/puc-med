package br.pucminas.pucmed.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.ui.extra.InternalButton;
import br.pucminas.pucmed.ui.extra.Notification.Type;

@SuppressWarnings("serial")
public abstract class BodyEdit extends VerticalLayout {
	private final Label message = new Label();
	private final FormLayout body = new FormLayout(message);
	private final HorizontalLayout buttons = new HorizontalLayout();
	private final InternalButton salvar = new InternalButton("Salvar");
	private final InternalButton cancelar = new InternalButton("Cancelar");

	public BodyEdit() {
		super();
		setSpacing(false);
		setSizeFull();
		body.setMargin(false);
		message.setVisible(false);
		Panel panel = new Panel(body);
		body.setSizeUndefined();
		body.setWidth("100%");
		panel.setSizeFull();
		panel.setStyleName(ValoTheme.PANEL_BORDERLESS);
		salvar.setIcon(VaadinIcons.CHECK);
		salvar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		cancelar.setIcon(VaadinIcons.CLOSE);
		cancelar.setStyleName(ValoTheme.BUTTON_DANGER);
		buttons.addComponents(salvar, cancelar);
		addComponents(panel, buttons);
		setExpandRatio(panel, 1);
	}

	public void addField(Component field) {
		body.addComponent(field);
	}

	public void addFields(Component... fields) {
		body.addComponents(fields);
	}

	public void setSalvarListener(ClickListener clickListener) {
		salvar.setClickListener(clickListener);
	}

	public void setCancelarListener(ClickListener clickListener) {
		cancelar.setClickListener(clickListener);
	}

	public void showMessage(String text, Type type) {
		message.setValue(text);
		switch (type) {
		case ERROR:
			message.setStyleName(ValoTheme.LABEL_FAILURE);
			break;
		case INFORMATION:
			message.setStyleName(ValoTheme.LABEL_SUCCESS);
			break;
		}
		message.setVisible(true);
	}

	public void hideMessage() {
		message.setVisible(false);
		message.setValue(null);
		message.setStyleName(null);
	}
}
