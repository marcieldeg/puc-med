package br.pucminas.pucmed.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.pucminas.pucmed.authentication.UserSession;
import br.pucminas.pucmed.ui.forms.AgendaForm;
import br.pucminas.pucmed.ui.forms.AtendimentoForm;
import br.pucminas.pucmed.ui.forms.EspecialidadeForm;
import br.pucminas.pucmed.ui.forms.ExameForm;
import br.pucminas.pucmed.ui.forms.LaboratorioForm;
import br.pucminas.pucmed.ui.forms.MedicamentoForm;
import br.pucminas.pucmed.ui.forms.MedicoForm;
import br.pucminas.pucmed.ui.forms.PacienteForm;
import br.pucminas.pucmed.ui.forms.RecepcionistaForm;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
@SpringView
public class MainView extends VerticalLayout implements View {
	public static final String NAME = "index";

	private final VerticalLayout layout = new VerticalLayout();
	private final TabSheet tabSheet = new TabSheet();
	private final List<Component> forms = new ArrayList<>();
	private final MenuBar menubar = new MenuBar();
	private final HorizontalLayout touchMenu = new HorizontalLayout();
	private final MenuBar touchMenubar = new MenuBar();

	private WelcomeLayout welcome = null;

	public MainView() {
		setMargin(false);
		setSpacing(false);
		setSizeFull();

		createTouchMenu();

		layout.setSpacing(false);
		layout.setMargin(false);
		layout.addComponent(createMenu());
		layout.setSizeFull();
		layout.addStyleName("area-trabalho");
		tabSheet.addTab(layout, "Área de Trabalho");
		tabSheet.setHeight(100.0f, Unit.PERCENTAGE);
		tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);

		addComponent(tabSheet);
		setExpandRatio(tabSheet, 3f);

		refreshMenuPermissions();
	}

	private void createTouchMenu() {
		touchMenu.setWidth("100%");
		touchMenu.setStyleName(ValoTheme.LAYOUT_CARD);
		touchMenu.addStyleName(ValoTheme.MENU_ROOT);
		touchMenu.addStyleName("touch-menu");

		touchMenubar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		MenuItem grupoMenu = touchMenubar.addItem("Menu", null);
		grupoMenu.setIcon(VaadinIcons.MENU);

		MenuItem i = grupoMenu.addItem("Serviços", null);
		i.setEnabled(false);
		i.setStyleName("menu-grupo");
		//grupoMenu.addSeparator();
		grupoMenu.addItem("Agendamento", e -> createTab(AgendaForm.class, AgendaForm.CAPTION))
				.setIcon(VaadinIcons.NOTEBOOK);
		grupoMenu.addItem("Atendimento", e -> createTab(AtendimentoForm.class, AtendimentoForm.CAPTION))
				.setIcon(VaadinIcons.AMBULANCE);
		grupoMenu.addItem("Pacientes", e -> createTab(PacienteForm.class, PacienteForm.CAPTION))
				.setIcon(VaadinIcons.USER_HEART);
		grupoMenu.addItem("Exames", e -> createTab(ExameForm.class, ExameForm.CAPTION))
				.setIcon(VaadinIcons.DOCTOR_BRIEFCASE);
		grupoMenu.addSeparator();
		i = grupoMenu.addItem("Pessoal", null);
		i.setEnabled(false);
		i.setStyleName("menu-grupo");
		//grupoMenu.addSeparator();
		grupoMenu.addItem("Recepcionistas", e -> createTab(RecepcionistaForm.class, RecepcionistaForm.CAPTION))
				.setIcon(VaadinIcons.HEADSET);
		grupoMenu.addItem("Laboratórios", e -> createTab(LaboratorioForm.class, LaboratorioForm.CAPTION))
				.setIcon(VaadinIcons.SPECIALIST);
		grupoMenu.addItem("Médicos", e -> createTab(MedicoForm.class, MedicoForm.CAPTION)).setIcon(VaadinIcons.DOCTOR);
		grupoMenu.addItem("Especialidades", e -> createTab(EspecialidadeForm.class, EspecialidadeForm.CAPTION))
				.setIcon(VaadinIcons.DIPLOMA_SCROLL);
		grupoMenu.addSeparator();
		i = grupoMenu.addItem("Materiais", null);
		i.setEnabled(false);
		i.setStyleName("menu-grupo");
		//grupoMenu.addSeparator();
		grupoMenu.addItem("Medicamentos", e -> createTab(MedicamentoForm.class, MedicamentoForm.CAPTION))
				.setIcon(VaadinIcons.PILL);

		touchMenu.addComponent(touchMenubar);

		Button title = new Button(Constants.APPLICATION_TITLE, e -> tabSheet.setSelectedTab(0));
		title.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		title.setSizeUndefined();
		touchMenu.addComponent(title);
		touchMenu.setComponentAlignment(title, Alignment.MIDDLE_CENTER);

		MenuBar menuSair = new MenuBar();
		menuSair.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		menuSair.addItem("Sair", e -> logOff()).setIcon(VaadinIcons.EXIT);
		touchMenu.addComponent(menuSair);

		touchMenu.setExpandRatio(title, 1f);

		addComponent(touchMenu);
	}

	private Component createMenu() {
		menubar.setWidth("100%");
		menubar.addStyleName("desk-menu");

		MenuItem grupoMenu = menubar.addItem("Serviços", null);
		grupoMenu.addItem("Agendamento", e -> createTab(AgendaForm.class, AgendaForm.CAPTION))
				.setIcon(VaadinIcons.NOTEBOOK);
		grupoMenu.addItem("Atendimento", e -> createTab(AtendimentoForm.class, AtendimentoForm.CAPTION))
				.setIcon(VaadinIcons.AMBULANCE);
		grupoMenu.addItem("Pacientes", e -> createTab(PacienteForm.class, PacienteForm.CAPTION))
				.setIcon(VaadinIcons.USER_HEART);
		grupoMenu.addItem("Exames", e -> createTab(ExameForm.class, ExameForm.CAPTION))
				.setIcon(VaadinIcons.DOCTOR_BRIEFCASE);

		grupoMenu = menubar.addItem("Pessoal", null);

		grupoMenu.addItem("Recepcionistas", e -> createTab(RecepcionistaForm.class, RecepcionistaForm.CAPTION))
				.setIcon(VaadinIcons.HEADSET);
		grupoMenu.addItem("Laboratórios", e -> createTab(LaboratorioForm.class, LaboratorioForm.CAPTION))
				.setIcon(VaadinIcons.SPECIALIST);
		grupoMenu.addItem("Médicos", e -> createTab(MedicoForm.class, MedicoForm.CAPTION)).setIcon(VaadinIcons.DOCTOR);
		grupoMenu.addItem("Especialidades", e -> createTab(EspecialidadeForm.class, EspecialidadeForm.CAPTION))
				.setIcon(VaadinIcons.DIPLOMA_SCROLL);

		grupoMenu = menubar.addItem("Materiais", null);
		grupoMenu.addItem("Medicamentos", e -> createTab(MedicamentoForm.class, MedicamentoForm.CAPTION))
				.setIcon(VaadinIcons.PILL);

		menubar.addItem("Sair", e -> logOff());

		return menubar;
	}

	private void setEnabledByName(String name, boolean enabled) {
		for (MenuItem i : menubar.getItems()) {
			if (!i.hasChildren())
				continue;
			for (MenuItem j : i.getChildren())
				if (j.getText().equals(name))
					j.setEnabled(enabled);
		}

		for (MenuItem j : touchMenubar.getItems().get(0).getChildren())
			if (j.getText().equals(name))
				j.setEnabled(enabled);
	}

	public void refreshMenuPermissions() {
		if (!UserSession.exists())
			return;
		UserSession u = UserSession.get();
		setEnabledByName("Agendamento", u.isRecepcionistaRole());
		setEnabledByName("Atendimento", u.isMedicoRole());
		setEnabledByName("Pacientes", u.isRecepcionistaRole());
		setEnabledByName("Exames", u.isLaboratorioRole());
		setEnabledByName("Recepcionistas", u.isRecepcionistaRole());
		setEnabledByName("Laboratórios", u.isRecepcionistaRole());
		setEnabledByName("Médicos", u.isRecepcionistaRole());
		setEnabledByName("Especialidades", u.isRecepcionistaRole());
		setEnabledByName("Medicamentos", u.isMedicoRole());
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T getForm(Class<T> formClass) {
		for (Component c : forms)
			if (formClass.isInstance(c))
				return (T) c;

		return null;
	}

	private <T extends Component> T addForm(Class<T> formClass) {
		try {
			T c = formClass.newInstance();
			forms.add(c);
			return c;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Not a valid layout");
		}
	}

	private <T extends Component> void createTab(Class<T> formClass, String name) {
		T c = getForm(formClass);
		if (c == null)
			c = addForm(formClass);

		Tab tab = tabSheet.addTab(c, name);
		tab.setClosable(true);
		tabSheet.setSelectedTab(tab);
	}

	private void logOff() {
		UserSession.set(null);
		layout.removeComponent(welcome);
		VaadinSession.getCurrent().close();
		Page.getCurrent().reload();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (!UserSession.exists())
			getUI().getNavigator().navigateTo(LoginView.NAME);

		refreshMenuPermissions();
		welcome = new WelcomeLayout();
		layout.addComponent(welcome);
		layout.setExpandRatio(welcome, 2L);
	}
}
