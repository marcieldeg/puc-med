package br.pucminas.pucmed.ui.extra;

import com.vaadin.ui.themes.ValoTheme;

import de.steinwedel.messagebox.ButtonOption;

public class MessageBox {
	public static void showQuestion(String message, Runnable onOkClick) {
		de.steinwedel.messagebox.MessageBox.createQuestion()//
				.asModal(true)//
				.withOkButton(onOkClick, ButtonOption.caption("Sim"), ButtonOption.icon(null),
						ButtonOption.style(ValoTheme.BUTTON_PRIMARY))//
				.withCancelButton(ButtonOption.caption("Não"), ButtonOption.icon(null))//
				.withCaption("Confirmação")//
				.withMessage(message)//
				.open();
	}
}
