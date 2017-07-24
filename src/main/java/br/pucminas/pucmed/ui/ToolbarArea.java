package br.pucminas.pucmed.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.ui.extra.InternalButton;

@SuppressWarnings("serial")
public class ToolbarArea extends HorizontalLayout {
	private final InternalButton botaoPesquisar = new InternalButton("Pesquisar");
	private final InternalButton botaoAdicionar = new InternalButton("Novo");
	private final InternalButton botaoEditar = new InternalButton("Editar");
	private final InternalButton botaoExcluir = new InternalButton("Excluir");

	private Map<String, InternalButton> customButtons = new HashMap<>();

	public ToolbarArea() {
		super();
		setSpacing(false);
		setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		botaoPesquisar.setIcon(VaadinIcons.SEARCH);
		botaoAdicionar.setIcon(VaadinIcons.PLUS);
		botaoEditar.setIcon(VaadinIcons.EDIT);
		botaoEditar.setEnabled(false);
		botaoExcluir.setIcon(VaadinIcons.ERASER);
		botaoExcluir.setEnabled(false);
		addComponents(botaoPesquisar, botaoAdicionar, botaoEditar, botaoExcluir);
	}

	public void setPesquisarEnabled(boolean enabled) {
		botaoPesquisar.setEnabled(enabled);
	}

	public void setAdicionarEnabled(boolean enabled) {
		botaoAdicionar.setEnabled(enabled);
	}

	public void setEditarEnabled(boolean enabled) {
		botaoEditar.setEnabled(enabled);
	}

	public void setExcluirEnabled(boolean enabled) {
		botaoExcluir.setEnabled(enabled);
	}

	public Button addCustomButton(String caption) {
		InternalButton button = new InternalButton(caption);
		button.setIcon(VaadinIcons.CLOUD);
		button.setEnabled(false);
		customButtons.put(caption, button);
		addComponent(button);
		return button;
	}

	public Button getCustomButton(String caption) {
		return customButtons.get(caption);
	}

	public void setPesquisarListener(ClickListener clickListener) {
		botaoPesquisar.setClickListener(clickListener);
	}

	public void setAdicionarListener(ClickListener clickListener) {
		botaoAdicionar.setClickListener(clickListener);
	}

	public void setEditarListener(ClickListener clickListener) {
		botaoEditar.setClickListener(clickListener);
	}

	public void setExcluirListener(ClickListener clickListener) {
		botaoExcluir.setClickListener(clickListener);
	}
}