package br.pucminas.pucmed.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import br.pucminas.pucmed.utils.Constants;

//@Viewport("user-scalable=no,initial-scale=1.0,width=device-width")
@SpringUI
@Theme("puc-med")
public class NavigatorUI extends UI {
	private static final long serialVersionUID = -9025178974527284267L;

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
