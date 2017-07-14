package br.pucminas.pucmed.ui.forms;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Atendimento;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Paciente;
import br.pucminas.pucmed.service.AtendimentoService;
import br.pucminas.pucmed.service.MedicoService;
import br.pucminas.pucmed.service.PacienteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.utils.MessageBox;
import br.pucminas.pucmed.utils.Constants;

public class AtendimentoForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private AtendimentoService service = BeanGetter.getService(AtendimentoService.class);
	private PacienteService pacienteService = BeanGetter.getService(PacienteService.class);
	private MedicoService medicoService = BeanGetter.getService(MedicoService.class);

	private Binder<Atendimento> binder = new Binder<>(Atendimento.class);

	private Grid<Atendimento> grid = new Grid<>(Atendimento.class);
	private TextField id = new TextField("Código");
	private ComboBox<Paciente> paciente = new ComboBox<>("Paciente");
	private DateField data = new DateField("Data");
	private TextArea descricao = new TextArea("Descrição");
	private TextField diagnostico = new TextField("Diagnóstico");
	private ComboBox<Medico> medico = new ComboBox<>("Médico");

	private ComboBox<Paciente> fPaciente = new ComboBox<>("Paciente");
	private DateField fData = new DateField("Data");

	public AtendimentoForm() {
		super("Cadastro de Atendimentos");

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn(o -> o.getPaciente() == null ? null : o.getPaciente().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Paciente");
		grid.addColumn("data").setWidth(Constants.LARGE_FIELD);
		grid.addColumn("descricao").setWidth(Constants.LARGE_FIELD).setCaption("Descrição");
		grid.addColumn("diagnostico").setWidth(Constants.LARGE_FIELD).setCaption("Diagnóstico");
		grid.addColumn(o -> o.getMedico() == null ? null : o.getMedico().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Médico");

		grid.addSelectionListener(e -> {
			Optional<Atendimento> atendimento = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(atendimento.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(atendimento.isPresent());
		});

		grid.setSizeFull();

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(paciente)//
				.asRequired("O campo é obrigatório")//
				.bind("paciente");
		binder.forField(data)//
				.withConverter(new LocalDateToDateConverter())//
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

		BodyView bodyView = new BodyView() {
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fPaciente, fData);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);
		id.setWidth(Constants.SMALL_FIELD + "px");
		paciente.setEmptySelectionAllowed(false);
		paciente.setItems(pacienteService.list());
		paciente.setItemCaptionGenerator(Paciente::getNome);
		descricao.setWidth(Constants.LARGE_FIELD + "px");
		diagnostico.setWidth(Constants.LARGE_FIELD + "px");
		medico.setEmptySelectionAllowed(false);
		medico.setItems(medicoService.list());
		medico.setItemCaptionGenerator(Medico::getNome);
		
		fPaciente.setEmptySelectionAllowed(false);
		fPaciente.setItems(pacienteService.list());
		fPaciente.setItemCaptionGenerator(Paciente::getNome);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

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
		List<Atendimento> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Atendimento> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		data.setValue(LocalDate.now());
		edit();
	}

	private void salvar() {
		Atendimento atendimento = new Atendimento();
		if (binder.writeBeanIfValid(atendimento)) {
			if (atendimento.getId() == null) {
				service.insert(atendimento);
			} else {
				service.update(atendimento);
			}
			updateGrid();
			view();
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fPaciente.isEmpty())
			params.put("paciente", fPaciente.getValue());
		if (!fData.isEmpty())
			params.put("data", fData.getValue());
		updateGrid(params);
	}

	private void limpar() {
		fPaciente.clear();
		fData.clear();
		updateGrid();
	}
}
