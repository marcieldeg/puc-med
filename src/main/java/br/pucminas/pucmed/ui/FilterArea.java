package br.pucminas.pucmed.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class FilterArea extends HorizontalLayout {

	public FilterArea() {
		super();
		setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
	}

	public void addFilter(Component field) {
		addComponent(field);
	}

	public void addFilters(Component... fields) {
		addComponents(fields);
	}

	public void removeFilter(Component field) {
		removeComponent(field);
	}
}