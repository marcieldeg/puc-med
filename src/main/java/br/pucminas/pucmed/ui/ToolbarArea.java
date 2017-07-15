package br.pucminas.pucmed.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.ui.extra.InternalButton;

public class ToolbarArea extends HorizontalLayout {
	private static final long serialVersionUID = -1687384805810835459L;

	private final InternalButton botaoAdicionar = new InternalButton("Novo");
	private final InternalButton botaoEditar = new InternalButton("Editar");
	private final InternalButton botaoExcluir = new InternalButton("Excluir");

	private Map<String, InternalButton> customButtons = new HashMap<>();

	public ToolbarArea() {
		super();
		setSpacing(false);
		setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		botaoAdicionar.setIcon(VaadinIcons.PLUS);
		botaoEditar.setIcon(VaadinIcons.EDIT);
		botaoEditar.setEnabled(false);
		botaoExcluir.setIcon(VaadinIcons.ERASER);
		botaoExcluir.setEnabled(false);
		addComponents(botaoAdicionar, botaoEditar, botaoExcluir);
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