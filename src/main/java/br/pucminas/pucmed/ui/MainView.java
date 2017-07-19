package br.pucminas.pucmed.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.authentication.UserSession;
import br.pucminas.pucmed.ui.forms.AgendaForm;
import br.pucminas.pucmed.ui.forms.AtendenteForm;
import br.pucminas.pucmed.ui.forms.AtendimentoForm;
import br.pucminas.pucmed.ui.forms.EspecialidadeForm;
import br.pucminas.pucmed.ui.forms.ExameForm;
import br.pucminas.pucmed.ui.forms.MedicamentoForm;
import br.pucminas.pucmed.ui.forms.MedicoForm;
import br.pucminas.pucmed.ui.forms.PacienteForm;

@SuppressWarnings("serial")
@SpringView
public class MainView extends VerticalLayout implements View {
	public static final String NAME = "index";

	private final VerticalLayout layout = new VerticalLayout();
	private final TabSheet tabSheet = new TabSheet();
	private final List<Component> forms = new ArrayList<>();
	private final MenuBar menubar = new MenuBar();

	private WelcomeLayout welcome = null;

	public MainView() {
		setMargin(false);
		setSpacing(false);
		setSizeFull();

		layout.setSpacing(false);
		layout.setMargin(false);
		layout.addComponent(createMenu());
		tabSheet.addTab(layout, "Área de Trabalho");
		tabSheet.setHeight(100.0f, Unit.PERCENTAGE);
		tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);

		addComponent(tabSheet);
		setExpandRatio(tabSheet, 3f);

		refreshMenuPermissions();
	}

	private Component createMenu() {
		menubar.setWidth("100%");

		MenuItem grupoMenu = menubar.addItem("Atendimento", null);
		grupoMenu.addItem("Agendamento", e -> createTab(AgendaForm.class, "Cadastro de Agendamentos"))
				.setIcon(VaadinIcons.NOTEBOOK);
		grupoMenu.addItem("Atendimento", e -> createTab(AtendimentoForm.class, "Cadastro de Atendimentos"))
				.setIcon(VaadinIcons.AMBULANCE);
		grupoMenu.addItem("Pacientes", e -> createTab(PacienteForm.class, "Cadastro de Pacientes"))
				.setIcon(VaadinIcons.USER_HEART);
		grupoMenu.addItem("Exames", e -> createTab(ExameForm.class, "Cadastro de Exames"))
				.setIcon(VaadinIcons.DOCTOR_BRIEFCASE);

		grupoMenu = menubar.addItem("Pessoal", null);

		grupoMenu.addItem("Atendentes", e -> createTab(AtendenteForm.class, "Cadastro de Atendentes"))
				.setIcon(VaadinIcons.USER_CARD);
		grupoMenu.addItem("Médicos", e -> createTab(MedicoForm.class, "Cadastro de Médicos"))
				.setIcon(VaadinIcons.DOCTOR);
		grupoMenu.addItem("Especialidades", e -> createTab(EspecialidadeForm.class, "Cadastro de Especialidades"))
				.setIcon(VaadinIcons.DIPLOMA_SCROLL);

		grupoMenu = menubar.addItem("Materiais", null);
		grupoMenu.addItem("Medicamentos", e -> createTab(MedicamentoForm.class, "Cadastro de Medicamentos"))
				.setIcon(VaadinIcons.PILL);

		menubar.addItem("Sair", e -> logOff());

		return menubar;
	}

	private MenuItem getChildByName(String name) {
		for (MenuItem i : menubar.getItems()) {
			if (!i.hasChildren())
				continue;
			for (MenuItem j : i.getChildren())
				if (j.getText().equals(name))
					return j;
		}
		return null;
	}

	public void refreshMenuPermissions() {
		if (!UserSession.exists())
			return;
		UserSession u = UserSession.get();
		getChildByName("Agendamento").setEnabled(u.isAtendenteRole());
		getChildByName("Atendimento").setEnabled(u.isMedicoRole());
		getChildByName("Pacientes").setEnabled(u.isAtendenteRole());
		getChildByName("Exames").setEnabled(u.isAtendenteRole());
		getChildByName("Atendentes").setEnabled(u.isAtendenteRole());
		getChildByName("Médicos").setEnabled(u.isAtendenteRole());
		getChildByName("Especialidades").setEnabled(u.isAtendenteRole());
		getChildByName("Medicamentos").setEnabled(u.isMedicoRole());
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T getForm(Class<T> formClass) {
		for (Component c : forms)
			if (formClass.isInstance(c))
				return (T) c;

		return null;
	}

	private <T extends Component> void createTab(Class<T> formClass, String name) {
		T c = getForm(formClass);
		if (c == null) {
			try {
				c = formClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Not a valid layout");
			}
			forms.add(c);
		}

		// TODO: verificar se aba está aberta

		Tab tab = tabSheet.addTab(c, name);
		tab.setClosable(true);
		tabSheet.setSelectedTab(tab);
	}

	private void logOff() {
		UserSession.set(null);
		layout.removeComponent(welcome);
		getUI().getNavigator().navigateTo(LoginView.NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (!UserSession.exists())
			getUI().getNavigator().navigateTo(LoginView.NAME);

		refreshMenuPermissions();
		welcome = new WelcomeLayout();
		layout.addComponent(welcome);
	}
}
