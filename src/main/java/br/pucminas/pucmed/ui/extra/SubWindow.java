package br.pucminas.pucmed.ui.extra;

import com.vaadin.ui.Window;

import br.pucminas.pucmed.ui.BaseForm;

@SuppressWarnings("serial")
public class SubWindow extends Window {
	public SubWindow(String caption, BaseForm baseForm) {
		super(caption);
		center();
		setContent(baseForm);
		setVisible(true);
		//setWidth("75%");
		//setHeight("75%");
		setStyleName("subwindow");
	}
}