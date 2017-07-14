package br.pucminas.pucmed.ui;

import com.vaadin.ui.Panel;

public abstract class BaseForm extends Panel {
	private static final long serialVersionUID = -8362608289138855719L;

	private BodyView bodyView;
	private BodyEdit bodyEdit;

	protected BaseForm(String caption) {
		super();
		setSizeFull();
		setCaption(caption);
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
