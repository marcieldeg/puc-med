package br.pucminas.pucmed.ui;

import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public abstract class BaseForm extends Panel {
	private BodyView bodyView;
	private BodyEdit bodyEdit;

	protected BaseForm(String caption) {
		super(caption);
		setSizeFull();
		setStyleName(ValoTheme.PANEL_BORDERLESS);
	}

	protected void edit() {
		if (bodyEdit == null)
			throw new RuntimeException("Body not set.");
		setContent(bodyEdit);
	}

	protected void view() {
		if (bodyView == null)
			throw new RuntimeException("View not set.");
		setContent(bodyView);
	}

	public BodyView getBodyView() {
		return this.bodyView;
	}

	public BodyEdit getBodyEdit() {
		return this.bodyEdit;
	}

	public void setBodyView(BodyView bodyView) {
		this.bodyView = bodyView;
	}

	public void setBodyEdit(BodyEdit bodyEdit) {
		this.bodyEdit = bodyEdit;
	}
}
