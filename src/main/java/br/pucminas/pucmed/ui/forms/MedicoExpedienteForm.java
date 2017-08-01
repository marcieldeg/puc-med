package br.pucminas.pucmed.ui.forms;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.MedicoExpediente;
import br.pucminas.pucmed.model.MedicoExpediente.DiaSemana;
import br.pucminas.pucmed.model.MedicoExpediente.Turno;
import br.pucminas.pucmed.service.MedicoExpedienteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

@SuppressWarnings("serial")
public class MedicoExpedienteForm extends BaseForm {
	private Medico medico;

	private MedicoExpedienteService service = BeanGetter.getService(MedicoExpedienteService.class);

	private Binder<MedicoExpediente> binder = new Binder<>(MedicoExpediente.class);

	private Grid<MedicoExpediente> grid = new Grid<>(MedicoExpediente.class);
	private TextField id = new TextField("Código");
	private ComboBox<DiaSemana> diaSemana = new ComboBox<>("Dia da Semana");
	private ComboBox<Turno> turno = new ComboBox<>("Turno");

	private ComboBox<DiaSemana> fDiaSemana = new ComboBox<>("Dia da Semana");
	private ComboBox<Turno> fTurno = new ComboBox<>("Turno");

	public static final String CAPTION = "Cadastro de Expedientes";

	public MedicoExpedienteForm(Medico medico) {
		super(CAPTION);

		this.medico = medico;

		updateGrid();
		grid.setColumns("id", "diaSemana", "turno");
		grid.getColumn("id")//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD);
		grid.getColumn("diaSemana")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("Dia da Semana");
		grid.getColumn("turno")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<MedicoExpediente> medicoExpediente = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(medicoExpediente.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(medicoExpediente.isPresent());
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(1);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(diaSemana)//
				.asRequired("O campo é obrigatório")//
				.bind("diaSemana");
		binder.forField(turno)//
				.asRequired("O campo é obrigatório")//
				.bind("turno");

		fDiaSemana.setItems(EnumSet.allOf(DiaSemana.class));
		fTurno.setItems(EnumSet.allOf(Turno.class));

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				fDiaSemana.addValueChangeListener(e -> pesquisar());
				fTurno.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fDiaSemana, fTurno);
			}
		};

		id.setEnabled(false);
		diaSemana.setItems(EnumSet.allOf(DiaSemana.class));
		diaSemana.setEmptySelectionAllowed(false);
		turno.setItems(EnumSet.allOf(Turno.class));
		turno.setEmptySelectionAllowed(false);

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		diaSemana.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		turno.addStyleName(Constants.MEDIUM_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, diaSemana, turno);

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
		updateGrid(new HashMap<>());
	}

	private void updateGrid(Map<String, Object> params) {
		params.put("medico", medico);
		List<MedicoExpediente> medicoExpedientes = service.list(params);
		grid.setItems(medicoExpedientes);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		MedicoExpediente medicoExpediente = new MedicoExpediente();
		medicoExpediente.setMedico(medico);
		if (binder.writeBeanIfValid(medicoExpediente)) {
			try {
				if (medicoExpediente.getId() == null)
					service.insert(medicoExpediente);
				else
					service.update(medicoExpediente);
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
		if (!fDiaSemana.isEmpty())
			params.put("diaSemana", fDiaSemana.getValue());
		if (!fTurno.isEmpty())
			params.put("turno", fTurno.getValue());
		updateGrid(params);
	}
}
