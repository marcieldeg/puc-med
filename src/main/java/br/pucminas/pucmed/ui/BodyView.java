package br.pucminas.pucmed.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public abstract class BodyView extends VerticalLayout {
	private final FilterArea filterArea = new FilterArea();
	private final Panel body = new Panel();
	private final ToolbarArea toolbarArea = new ToolbarArea();

	public BodyView() {
		super();
		setSpacing(false);
		setMargin(true);
		setSizeFull();

		addComponent(filterArea);
		body.setStyleName(ValoTheme.PANEL_BORDERLESS);
		body.setSizeFull();
		addComponent(body);
		addComponent(toolbarArea);

		setExpandRatio(body, 1);
	}

	public FilterArea getFilterArea() {
		return filterArea;
	}

	public ToolbarArea getToolbarArea() {
		return toolbarArea;
	}

	public void setBody(Component component) {
		body.setContent(component);
	}
}
