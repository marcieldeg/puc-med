package br.pucminas.pucmed.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class FilterArea extends HorizontalLayout {
	private static final long serialVersionUID = 7540088915648392996L;

	private final Button botaoPesquisar = new Button("Pesquisar");
	private Registration removePesquisarListener;
	private final Button botaoLimpar = new Button("Limpar");
	private Registration removeLimparListener;

	public FilterArea() {
		super();
		setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		addButtons();
	}

	private void removeButtons() {
		removeComponent(botaoPesquisar);
		removeComponent(botaoLimpar);
	}

	private void addButtons() {
		botaoPesquisar.setIcon(VaadinIcons.SEARCH);
		addComponent(botaoPesquisar);
		setComponentAlignment(botaoPesquisar, Alignment.BOTTOM_RIGHT);
		botaoLimpar.setIcon(VaadinIcons.CLOSE_SMALL);
		addComponent(botaoLimpar);
		setComponentAlignment(botaoLimpar, Alignment.BOTTOM_RIGHT);
	}

	public void addFilter(Component field) {
		removeButtons();
		addComponent(field);
		addButtons();
	}

	public void addFilters(Component... fields) {
		removeButtons();
		addComponents(fields);
		addButtons();
	}

	public void removeFilter(Component field) {
		removeComponent(field);
	}

	public void setPesquisarListener(ClickListener clickListener) {
		if (removePesquisarListener != null)
			removePesquisarListener.remove();
		removePesquisarListener = botaoPesquisar.addClickListener(clickListener);
	}

	public void setLimparListener(ClickListener clickListener) {
		if (removeLimparListener != null)
			removeLimparListener.remove();
		removeLimparListener = botaoLimpar.addClickListener(clickListener);
	}
}