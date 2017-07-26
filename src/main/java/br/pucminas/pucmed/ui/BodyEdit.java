package br.pucminas.pucmed.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.ui.extra.InternalButton;

@SuppressWarnings("serial")
public abstract class BodyEdit extends VerticalLayout {
	private final FormLayout body = new FormLayout();
	private final HorizontalLayout buttons = new HorizontalLayout();
	private final InternalButton salvar = new InternalButton("Salvar");
	private final InternalButton cancelar = new InternalButton("Cancelar");

	public BodyEdit() {
		super();
		setSpacing(false);
		setSizeFull();
		Panel panel = new Panel(body);
		body.setSizeUndefined();
		body.setWidth("100%");
		panel.setSizeFull();
		panel.setStyleName(ValoTheme.PANEL_BORDERLESS);
		salvar.setIcon(VaadinIcons.CHECK);
		salvar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		cancelar.setIcon(VaadinIcons.CLOSE);
		cancelar.setStyleName(ValoTheme.BUTTON_DANGER);
		// buttons.setSpacing(false);
		buttons.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
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
}
