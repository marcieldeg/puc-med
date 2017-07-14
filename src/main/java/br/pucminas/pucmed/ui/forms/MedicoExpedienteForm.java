package br.pucminas.pucmed.ui.forms;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import br.pucminas.pucmed.ui.utils.MessageBox;
import br.pucminas.pucmed.utils.Constants;

public class MedicoExpedienteForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private Medico medico;

	private MedicoExpedienteService service = BeanGetter.getService(MedicoExpedienteService.class);

	private Binder<MedicoExpediente> binder = new Binder<>(MedicoExpediente.class);

	private Grid<MedicoExpediente> grid = new Grid<>(MedicoExpediente.class);
	private TextField id = new TextField("Código");
	private ComboBox<DiaSemana> diaSemana = new ComboBox<>("Dia da Semana");
	private ComboBox<Turno> turno = new ComboBox<>("Turno");

	private ComboBox<DiaSemana> fDiaSemana = new ComboBox<>("Dia da Semana");
	private ComboBox<Turno> fTurno = new ComboBox<>("Turno");

	public MedicoExpedienteForm(Medico medico) {
		super("Cadastro de Expedientes");

		this.medico = medico;

		updateGrid();
		grid.setColumns("id", "diaSemana", "turno");
		grid.getColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.getColumn("diaSemana").setWidth(Constants.LARGE_FIELD).setCaption("Dia da Semana");
		grid.getColumn("turno").setWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<MedicoExpediente> medicoExpediente = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(medicoExpediente.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(medicoExpediente.isPresent());
		});

		grid.setSizeFull();

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
		fDiaSemana.setEmptySelectionAllowed(false);
		fTurno.setItems(EnumSet.allOf(Turno.class));
		fTurno.setEmptySelectionAllowed(false);

		BodyView bodyView = new BodyView() {
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fDiaSemana, fTurno);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);

		diaSemana.setItems(EnumSet.allOf(DiaSemana.class));
		diaSemana.setEmptySelectionAllowed(false);
		turno.setItems(EnumSet.allOf(Turno.class));
		turno.setEmptySelectionAllowed(false);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

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
						service.delete(grid.asSingleSelect().getValue());
						updateGrid();
					});
		}
	}

	private void updateGrid() {
		List<MedicoExpediente> medicoExpedientes = service.list();
		grid.setItems(medicoExpedientes);
	}

	private void updateGrid(Map<String, Object> params) {
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
			if (medicoExpediente.getId() == null)
				service.insert(medicoExpediente);
			else
				service.update(medicoExpediente);
			updateGrid();
			view();
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

	private void limpar() {
		fDiaSemana.clear();
		fTurno.clear();
		updateGrid();
	}
}
