package br.pucminas.pucmed.ui.extra;

import com.vaadin.ui.Window;

import br.pucminas.pucmed.ui.BaseForm;

public class SubWindow extends Window {
	private static final long serialVersionUID = 6157453624162378876L;

	public SubWindow(String caption, BaseForm baseForm) {
		super(caption);
		center();
		setContent(baseForm);
		setVisible(true);
		setWidth("75%");
		setHeight("75%");
	}
}