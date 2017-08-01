package br.pucminas.pucmed.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.authentication.UserSession;
import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.email.Email;
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.service.UsuarioService;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
@SpringView
public class LoginView extends VerticalLayout implements View {
	public static final String NAME = "";

	private final TextField usuario = new TextField("Usuário:");
	private final PasswordField senha = new PasswordField("Senha:");

	private final Button entrar = new Button("Entrar", e -> entrarClick(e));
	private final Button recuperarSenha = new Button("Esqueceu sua senha? Clique aqui.", e -> recuperarSenhaClick(e));

	private final Window window = new RecuperarSenhaWindow();

	public LoginView() {
		Panel panel = new Panel();
		panel.setCaption(Constants.APPLICATION_TITLE + " - Entrar no sistema");
		panel.setResponsive(true);
		panel.setStyleName("responsive");

		VerticalLayout formLayout = new VerticalLayout();
		formLayout.setMargin(true);
		formLayout.setSizeFull();

		usuario.setWidth(100f, Unit.PERCENTAGE);
		formLayout.addComponent(usuario);

		senha.setWidth(100f, Unit.PERCENTAGE);
		formLayout.addComponent(senha);

		entrar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		entrar.setDisableOnClick(true);

		recuperarSenha.setStyleName(ValoTheme.BUTTON_LINK);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponentsAndExpand(entrar);
		horizontalLayout.setResponsive(true);
		formLayout.addComponent(horizontalLayout);

		panel.setContent(formLayout);

		addComponent(panel);
		setResponsive(true);
		addComponent(recuperarSenha);
		setComponentAlignment(recuperarSenha, Alignment.TOP_CENTER);

		usuario.focus();
	}

	private UsuarioService usuarioService = BeanGetter.getService(UsuarioService.class);

	private void entrarClick(ClickEvent event) {
		String usuario = this.usuario.getValue();
		String senha = this.senha.getValue();

		Usuario u = usuarioService.getByLogin(usuario, senha);
		if (u == null) {
			Notification.show("Erro", "Usuário ou senha incorretos", Type.ERROR);
			entrar.setEnabled(true);
			this.senha.focus();
		} else if (Status.INATIVO.equals(u.getStatus())) {
			Notification.show("Erro", "Usuário inativo", Type.ERROR);
			entrar.setEnabled(true);
			this.usuario.focus();
		} else {
			UserSession.set(new UserSession(u));

			getUI().getNavigator().navigateTo(MainView.NAME);
		}
	}

	private void recuperarSenhaClick(ClickEvent event) {
		getUI().addWindow(window);
	}

	private static class RecuperarSenhaWindow extends Window {
		private final VerticalLayout verticalLayout = new VerticalLayout();
		private final CssLayout cssLayout = new CssLayout();
		private final Label titulo = new Label("Informe seu e-mail:");
		private final Button ok = new Button("OK", e -> enviarEmail(e));
		private final TextField email = new TextField();

		public RecuperarSenhaWindow() {
			setCaption("Recuperação de senha");
			ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
			cssLayout.addComponents(email, ok);
			cssLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
			verticalLayout.addComponents(titulo, cssLayout);
			verticalLayout.setMargin(true);

			setModal(true);
			setVisible(true);
			setResizable(false);
			setContent(verticalLayout);
		}

		private void enviarEmail(ClickEvent event) {
			if (Email.isValid(email.getValue())) {
				getUI().removeWindow(this);
				Email.enviarSenha(email.getValue());
				Notification.show("Operação concluída",
						"Caso o email informado exista na base de usuários, a senha será enviada.", Type.INFORMATION);
			} else {
				email.focus();
				Notification.show("Erro", "O e-mail informado é inválido.", Type.ERROR);
			}
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (UserSession.exists())
			getUI().getNavigator().navigateTo(MainView.NAME);
	}
}
