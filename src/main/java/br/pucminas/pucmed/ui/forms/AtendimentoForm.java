package br.pucminas.pucmed.ui.forms;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.authentication.UserSession;
import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Atendimento;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Paciente;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.service.AtendimentoService;
import br.pucminas.pucmed.service.MedicoService;
import br.pucminas.pucmed.service.PacienteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.ui.extra.SubWindow;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

@SuppressWarnings("serial")
public class AtendimentoForm extends BaseForm {
	private AtendimentoService service = BeanGetter.getService(AtendimentoService.class);
	private PacienteService pacienteService = BeanGetter.getService(PacienteService.class);
	private MedicoService medicoService = BeanGetter.getService(MedicoService.class);

	private Binder<Atendimento> binder = new Binder<>(Atendimento.class);

	private Grid<Atendimento> grid = new Grid<>(Atendimento.class);
	private TextField id = new TextField("Código");
	private ComboBox<Paciente> paciente = new ComboBox<>("Paciente");
	private DateTimeField data = new DateTimeField("Data");
	private TextArea descricao = new TextArea("Descrição");
	private TextField diagnostico = new TextField("Diagnóstico");
	private ComboBox<Medico> medico = new ComboBox<>("Médico");

	private ComboBox<Paciente> fPaciente = new ComboBox<>("Paciente");
	private DateField fData = new DateField("Data");

	UserSession userSession = UserSession.get();

	public static final String CAPTION = "Cadastro de Atendimentos";

	public AtendimentoForm() {
		super(CAPTION);

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id")//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD);
		grid.addColumn(o -> o.getPaciente() == null ? null : o.getPaciente().getNome())//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("Paciente");
		grid.addColumn(//
				o -> {
					if (o.getData() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					return format.format(o.getData());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Data");
		grid.addColumn("descricao")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("Descrição");
		grid.addColumn("diagnostico")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.XLARGE_FIELD)//
				.setCaption("Diagnóstico");
		grid.addColumn(o -> o.getMedico() == null ? null : o.getMedico().getNome())//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("Médico");

		grid.addSelectionListener(e -> {
			Optional<Atendimento> atendimento = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(atendimento.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(atendimento.isPresent());
			getBodyView().getToolbarArea().getCustomButton("Exames").setEnabled(atendimento.isPresent());
			getBodyView().getToolbarArea().getCustomButton("Receituário").setEnabled(atendimento.isPresent());
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(1);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(paciente)//
				.asRequired("O campo é obrigatório")//
				.bind("paciente");
		binder.forField(data)//
				.withConverter(new LocalDateTimeToDateConverter(ZoneId.systemDefault()))//
				.asRequired("O campo é obrigatório")//
				.bind("data");
		binder.forField(descricao)//
				.asRequired("O campo é obrigatório")//
				.bind("descricao");
		binder.forField(diagnostico)//
				.asRequired("O campo é obrigatório")//
				.bind("diagnostico");
		binder.forField(medico)//
				.asRequired("O campo é obrigatório")//
				.bind("medico");

		fPaciente.setItems(pacienteService.list());
		fPaciente.setItemCaptionGenerator(Paciente::getNome);

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());
				Button botaoExames = getToolbarArea().addCustomButton("Exames");
				botaoExames.setIcon(VaadinIcons.DOCTOR_BRIEFCASE);
				botaoExames.addClickListener(e -> abrirExames());
				Button botaoReceituarios = getToolbarArea().addCustomButton("Receituário");
				botaoReceituarios.setIcon(VaadinIcons.PILLS);
				botaoReceituarios.addClickListener(e -> abrirReceituarios());

				fPaciente.addValueChangeListener(e -> pesquisar());
				fData.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fPaciente, fData);
			}
		};

		id.setEnabled(false);
		paciente.setEmptySelectionAllowed(false);
		paciente.setItems(pacienteService.list());
		paciente.setItemCaptionGenerator(Paciente::getNome);
		medico.setEmptySelectionAllowed(false);
		medico.setItems(medicoService.list());
		medico.setItemCaptionGenerator(Medico::getNome);
		Usuario usuario = UserSession.get().getUsuario();
		if (usuario instanceof Medico) {
			medico.setSelectedItem((Medico) usuario);
			medico.setEnabled(false);
		}

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		paciente.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		data.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		descricao.addStyleName(Constants.XLARGE_FIELD_STYLE);
		diagnostico.addStyleName(Constants.XLARGE_FIELD_STYLE);
		medico.addStyleName(Constants.MEDIUM_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, paciente, data, descricao, diagnostico, medico);

				setSalvarListener(e -> salvar());
				setCancelarListener(e -> view());
			}
		};

		setBodyEdit(bodyEdit);
		setBodyView(bodyView);

		view();
	}

	private void abrirExames() {
		SubWindow subWindow = new SubWindow(" Exames", new ExameForm(grid.asSingleSelect().getValue()));
		subWindow.addCloseListener(o -> getUI().removeWindow(subWindow));
		subWindow.setIcon(VaadinIcons.DOCTOR_BRIEFCASE);
		getUI().addWindow(subWindow);
	}

	private void abrirReceituarios() {
		SubWindow subWindow = new SubWindow(" Receituário", new ReceituarioForm(grid.asSingleSelect().getValue()));
		subWindow.addCloseListener(o -> getUI().removeWindow(subWindow));
		subWindow.setIcon(VaadinIcons.PILLS);
		getUI().addWindow(subWindow);
	}

	private void editar() {
		if (!grid.asSingleSelect().isEmpty()) {
			binder.setBean(grid.asSingleSelect().getValue());
			edit();
		}
	}

	private void excluir() {
		if (!grid.asSingleSelect().isEmpty()) {
			MessageBox.showQuestion("Confirma a exclusão desse registro?", //
					() -> {
						try {
							service.delete(grid.asSingleSelect().getValue());
							updateGrid();
						} catch (DataIntegrityViolationException ex) {
							Throwable cause = ex.getMostSpecificCause();
							String message = "";
							if (cause instanceof PSQLException)
								message = Utils.translateExceptionMessage((PSQLException) cause);
							else
								message = cause.getLocalizedMessage();
							Notification.show(message, Type.ERROR);
						}
					});
		}
	}

	private void updateGrid() {
		List<Atendimento> usuarios;
		if (userSession.getUsuario() instanceof Medico) {
			Map<String, Object> params = new HashMap<>();
			params.put("medico", (Medico) userSession.getUsuario());
			usuarios = service.list(params);
		} else {
			usuarios = service.list();
		}
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		if (userSession.getUsuario() instanceof Medico)
			params.put("medico", (Medico) userSession.getUsuario());
		List<Atendimento> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		data.setValue(LocalDateTime.now());
		edit();
	}

	private void salvar() {
		Atendimento atendimento = new Atendimento();
		if (binder.writeBeanIfValid(atendimento)) {
			try {
				if (atendimento.getId() == null) {
					service.insert(atendimento);
				} else {
					service.update(atendimento);
				}
				updateGrid();
				view();
			} catch (DataIntegrityViolationException ex) {
				Throwable cause = ex.getMostSpecificCause();
				String message = "";
				if (cause instanceof PSQLException)
					message = Utils.translateExceptionMessage((PSQLException) cause);
				else
					message = cause.getLocalizedMessage();
				getBodyEdit().showMessage(message, Type.ERROR);
			}
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fPaciente.isEmpty())
			params.put("paciente", fPaciente.getValue());
		if (!fData.isEmpty()) {
			params.put("data#ge", Utils.convertLocalDateToDate(fData.getValue()));
			params.put("data#lt", Utils.convertLocalDateToDate(fData.getValue().plusDays(1)));
		}
		updateGrid(params);
	}
}
