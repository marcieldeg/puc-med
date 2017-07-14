package br.pucminas.pucmed.ui;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public class InternalButton extends Button {
	private static final long serialVersionUID = 8045098814681553038L;

	private Registration registration;

	public InternalButton(String caption) {
		super(caption);
		setStyleName(ValoTheme.BUTTON_QUIET);
	}

	public void setClickListener(ClickListener clickListener) {
		if (registration != null)
			registration.remove();
		registration = addClickListener(clickListener);
	}
}