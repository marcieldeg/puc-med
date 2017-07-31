package br.pucminas.pucmed.ui;

import com.vaadin.ui.Panel;

@SuppressWarnings("serial")
public abstract class BaseForm extends Panel {
	private BodyView bodyView;
	private BodyEdit bodyEdit;

	protected BaseForm(String caption) {
		super(caption);
		setSizeFull();
		setStyleName("panel-form");
	}

	protected void edit() {
		if (bodyEdit == null)
			throw new RuntimeException("Body not set.");
		bodyEdit.hideMessage();
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
