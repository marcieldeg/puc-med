package br.pucminas.pucmed.ui.forms;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Agenda;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.MedicoExpediente;
import br.pucminas.pucmed.model.MedicoExpediente.DiaSemana;
import br.pucminas.pucmed.model.MedicoExpediente.Turno;
import br.pucminas.pucmed.model.Paciente;
import br.pucminas.pucmed.service.AgendaService;
import br.pucminas.pucmed.service.MedicoService;
import br.pucminas.pucmed.service.PacienteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

public class AgendaForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private AgendaService service = BeanGetter.getService(AgendaService.class);
	private PacienteService pacienteService = BeanGetter.getService(PacienteService.class);
	private MedicoService medicoService = BeanGetter.getService(MedicoService.class);

	private Binder<Agenda> binder = new Binder<>(Agenda.class);

	private Grid<Agenda> grid = new Grid<>(Agenda.class);
	private TextField id = new TextField("Código");
	private DateTimeField data = new DateTimeField("Data");
	private ComboBox<Paciente> paciente = new ComboBox<>("Paciente");
	private ComboBox<Medico> medico = new ComboBox<>("Médico");

	private DateField fData = new DateField("Data");
	private ComboBox<Paciente> fPaciente = new ComboBox<>("Paciente");
	private ComboBox<Medico> fMedico = new ComboBox<>("Médico");

	public AgendaForm() {
		super();

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn(//
				o -> {
					if (o.getData() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
					return format.format(o.getData());
				})//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Data");
		grid.addColumn(o -> o.getPaciente().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Paciente");
		grid.addColumn(o -> o.getMedico().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Médico");

		grid.addSelectionListener(e -> {
			Optional<Agenda> agenda = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(agenda.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(agenda.isPresent());
		});

		grid.setSizeFull();

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(data)//
				.withConverter(new LocalDateTimeToDateConverter(Constants.ZONE_OFFSET))//
				.asRequired("O campo é obrigatório")//
				.bind("data");
		binder.forField(paciente)//
				.asRequired("O campo é obrigatório")//
				.bind("paciente");
		binder.forField(medico)//
				.asRequired("O campo é obrigatório")//
				.bind("medico");

		binder.withValidator(new Validator<Agenda>() {
			private static final long serialVersionUID = 6277993876834752213L;

			@Override
			public ValidationResult apply(Agenda s, ValueContext valueContext) {
				Set<MedicoExpediente> expedientes = s.getMedico().getExpediente();
				DiaSemana diaSemana = DiaSemana.fromDate(s.getData());

				Turno turno = Turno.INTEGRAL;
				try {
					turno = Turno.fromDate(s.getData());
				} catch (IndexOutOfBoundsException e) {
					Notification.show("Horário indisponível para agendamento", Type.ERROR);
					return ValidationResult.error("Horário indisponível para agendamento");
				}

				for (MedicoExpediente e : expedientes) {
					if (e.getDiaSemana().equals(diaSemana)) {
						if (e.getTurno().equals(Turno.INTEGRAL) || e.getTurno().equals(turno)) {
							return ValidationResult.ok();
						}
					}
				}

				Notification.show("Esse médico não atende no horário informado", Type.ERROR);
				return ValidationResult.error("Esse médico não atende no horário informado");
			}
		});

		List<Paciente> pacientes = pacienteService.list();
		List<Medico> medicos = medicoService.list();

		paciente.setEmptySelectionAllowed(false);
		paciente.setItems(pacientes);
		paciente.setItemCaptionGenerator(Paciente::getNome);

		medico.setEmptySelectionAllowed(false);
		medico.setItems(medicos);
		medico.setItemCaptionGenerator(Medico::getNome);

		fPaciente.setEmptySelectionAllowed(false);
		fPaciente.setItems(pacientes);
		fPaciente.setItemCaptionGenerator(Paciente::getNome);

		fMedico.setEmptySelectionAllowed(false);
		fMedico.setItems(medicos);
		fMedico.setItemCaptionGenerator(Medico::getNome);

		BodyView bodyView = new BodyView() {
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fData, fPaciente, fMedico);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

			{
				addFields(id, data, paciente, medico);

				setSalvarListener(e -> salvar());
				setCancelarListener(e -> view());
			}
		};

		setBodyEdit(bodyEdit);
		setBodyView(bodyView);

		view();
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
						service.delete(grid.asSingleSelect().getValue());
						updateGrid();
					});
		}
	}

	private void updateGrid() {
		List<Agenda> agendas = service.list();
		grid.setItems(agendas);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Agenda> agendas = service.list(params);
		grid.setItems(agendas);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Agenda agenda = new Agenda();
		if (binder.writeBeanIfValid(agenda)) {
			if (agenda.getId() == null)
				service.insert(agenda);
			else
				service.update(agenda);
			updateGrid();
			view();
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fData.isEmpty()) {
			params.put("data#ge", Utils.convertLocalDateToDate(fData.getValue()));
			params.put("data#lt", Utils.convertLocalDateToDate(fData.getValue().plusDays(1)));
		}
		if (!fPaciente.isEmpty())
			params.put("paciente", fPaciente.getValue());
		if (!fMedico.isEmpty())
			params.put("medico", fMedico.getValue());
		updateGrid(params);
	}

	private void limpar() {
		fData.clear();
		fPaciente.clear();
		fMedico.clear();
		updateGrid();
	}
}
