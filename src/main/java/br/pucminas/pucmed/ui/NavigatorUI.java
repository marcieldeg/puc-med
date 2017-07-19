package br.pucminas.pucmed.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
@SpringUI
@Theme("valo-facebook")
public class NavigatorUI extends UI {
	private Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {
		setResponsive(true);

		getPage().setTitle(Constants.APPLICATION_TITLE);

		navigator = new Navigator(this, this);

		navigator.addView(LoginView.NAME, LoginView.class);
		navigator.addView(MainView.NAME, MainView.class);
	}
}
