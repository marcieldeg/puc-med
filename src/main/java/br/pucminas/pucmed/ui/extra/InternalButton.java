package br.pucminas.pucmed.ui.extra;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class InternalButton extends Button {
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