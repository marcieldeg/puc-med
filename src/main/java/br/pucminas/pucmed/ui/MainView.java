package br.pucminas.pucmed.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.authentication.UserSession;
import br.pucminas.pucmed.ui.forms.AtendenteForm;
import br.pucminas.pucmed.ui.forms.AtendimentoForm;
import br.pucminas.pucmed.ui.forms.EspecialidadeForm;
import br.pucminas.pucmed.ui.forms.MedicamentoForm;
import br.pucminas.pucmed.ui.forms.MedicoForm;
import br.pucminas.pucmed.ui.forms.PacienteForm;
import br.pucminas.pucmed.ui.forms.UsuarioForm;

@SpringView
public class MainView extends VerticalLayout implements View {
	private static final long serialVersionUID = -7806307405081829079L;

	public static final String NAME = "index";

	// private final VerticalLayout body = new VerticalLayout();
	private final TabSheet tabSheet = new TabSheet();
	private final List<Component> forms = new ArrayList<>();

	public MainView() {
		setMargin(false);
		setSpacing(false);
		setSizeFull();

		tabSheet.addTab(createMenu(), "Área de Trabalho");
		tabSheet.setHeight(100.0f, Unit.PERCENTAGE);
		tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);

		addComponent(tabSheet);
		setExpandRatio(tabSheet, 3f);
	}

	private MenuBar createMenu() {
		MenuBar menubar = new MenuBar();
		menubar.setWidth("100%");
		MenuItem cadastro = menubar.addItem("Cadastro", null);
		cadastro.addItem("Atendimentos", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(AtendimentoForm.class, "Cadastro de Atendimentos");
			}
		});
		cadastro.addItem("Pacientes", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(PacienteForm.class, "Cadastro de Pacientes");
			}
		});
		cadastro.addItem("Usuários", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(UsuarioForm.class, "Cadastro de Usuários");
			}
		});
		cadastro.addItem("Médicos", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(MedicoForm.class, "Cadastro de Médicos");
			}
		});
		cadastro.addItem("Atendentes", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(AtendenteForm.class, "Cadastro de Atendentes");
			}
		});
		cadastro.addItem("Medicamentos", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(MedicamentoForm.class, "Cadastro de Medicamentos");
			}
		});

		cadastro.addItem("Especialidades", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				setBody(EspecialidadeForm.class, "Cadastro de Especialidades");
			}
		});
		MenuItem relatorio = menubar.addItem("Relatórios", null);
		relatorio.addItem("Pacientes", null);
		relatorio.addItem("Usuários", null);
		relatorio.addItem("Médicos", null);
		relatorio.addItem("Atendentes", null);
		relatorio.addItem("Medicamentos", null);

		menubar.addItem("Sair", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				logOff();
			}
		});

		return menubar;
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T getForm(Class<T> formClass) {
		for (Component c : forms)
			if (formClass.isInstance(c))
				return (T) c;

		return null;
	}

	private <T extends Component> void setBody(Class<T> formClass, String name) {
		T c = getForm(formClass);
		if (c == null) {
			try {
				c = formClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Not a valid layout");
			}
			forms.add(c);
		}

		Tab tab = tabSheet.addTab(c, name);
		tab.setClosable(true);
		tabSheet.setSelectedTab(tab);
	}

	private void logOff() {
		UserSession.set(null);
		getUI().getNavigator().navigateTo(LoginView.NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (!UserSession.exists())
			getUI().getNavigator().navigateTo(LoginView.NAME);
	}
}
