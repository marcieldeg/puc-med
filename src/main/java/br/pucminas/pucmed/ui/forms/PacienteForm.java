package br.pucminas.pucmed.ui.forms;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Estado;
import br.pucminas.pucmed.model.Paciente;
import br.pucminas.pucmed.model.Paciente.Sexo;
import br.pucminas.pucmed.service.EstadoService;
import br.pucminas.pucmed.service.PacienteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.utils.MessageBox;
import br.pucminas.pucmed.utils.Constants;

public class PacienteForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private PacienteService service = BeanGetter.getService(PacienteService.class);
	private EstadoService estadoService = BeanGetter.getService(EstadoService.class);

	private Binder<Paciente> binder = new Binder<>(Paciente.class);

	private Grid<Paciente> grid = new Grid<>(Paciente.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private ComboBox<Sexo> sexo = new ComboBox<>("Sexo");
	private DateField dataNascimento = new DateField("Data Nascimento");
	private TextField cpf = new TextField("CPF");
	private TextField endereco = new TextField("Endereço");
	private TextField numero = new TextField("Número");
	private TextField complemento = new TextField("Complemento");
	private TextField bairro = new TextField("Bairro");
	private TextField cidade = new TextField("cidade");
	private ComboBox<Estado> estado = new ComboBox<>("Estado");
	private TextField cep = new TextField("CEP");
	private TextField email = new TextField("E-mail");
	private TextField telefone = new TextField("Telefone");

	private TextField fNome = new TextField("Nome");

	public PacienteForm() {
		super("Cadastro de Pacientes");

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn("nome").setWidth(Constants.LARGE_FIELD);
		grid.addColumn(o -> o.getSexo().getValue())//
				.setWidth(Constants.SMALL_FIELD)//
				.setCaption("Sexo");
		grid.addColumn(//
				o -> {
					if (o.getDataNascimento() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					return format.format(o.getDataNascimento());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Data Nascimento");
		grid.addColumn(//
				o -> {
					if (o.getDataNascimento() == null)
						return null;

					return ChronoUnit.YEARS.between(
							o.getDataNascimento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
							LocalDate.now(ZoneId.systemDefault()));
				})//
				.setWidth(Constants.SMALL_FIELD)//
				.setCaption("Idade");
		grid.addColumn("cpf").setWidth(Constants.MEDIUM_FIELD).setCaption("CPF");
		grid.addColumn("endereco").setWidth(Constants.LARGE_FIELD).setCaption("Endereço");
		grid.addColumn("numero").setWidth(Constants.SMALL_FIELD).setCaption("Nº");
		grid.addColumn("complemento").setWidth(Constants.MEDIUM_FIELD).setCaption("Complemento");
		grid.addColumn("bairro").setWidth(Constants.MEDIUM_FIELD).setCaption("Bairro");
		grid.addColumn("cidade").setWidth(Constants.MEDIUM_FIELD).setCaption("Cidade");
		grid.addColumn(o -> o.getEstado() == null ? null : o.getEstado().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Estado");
		grid.addColumn("cep").setWidth(Constants.SMALL_FIELD).setCaption("CEP");
		grid.addColumn("email").setWidth(Constants.LARGE_FIELD).setCaption("E-mail");
		grid.addColumn("telefone").setWidth(Constants.MEDIUM_FIELD).setCaption("Telefone");

		grid.addSelectionListener(e -> {
			Optional<Paciente> paciente = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(paciente.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(paciente.isPresent());
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(2);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(nome)//
				.asRequired("O campo é obrigatório")//
				.bind("nome");
		binder.forField(sexo)//
				.asRequired("O campo é obrigatório")//
				.bind("sexo");
		binder.forField(dataNascimento)//
				.withConverter(new LocalDateToDateConverter())//
				.asRequired("O campo é obrigatório")//
				.bind("dataNascimento");
		binder.forField(cpf)//
				.asRequired("O campo é obrigatório")//
				.bind("cpf");
		binder.forField(endereco)//
				.bind("endereco");
		binder.forField(numero)//
				.withConverter(new StringToLongConverter("Número Inválido"))//
				.bind("numero");
		binder.forField(complemento)//
				.bind("complemento");
		binder.forField(bairro)//
				.bind("bairro");
		binder.forField(cidade)//
				.bind("cidade");
		binder.forField(estado)//
				.bind("estado");
		binder.forField(cep)//
				.bind("cep");
		binder.forField(email)//
				.withValidator(new EmailValidator("E-mail inválido"))//
				.bind("email");
		binder.forField(telefone)//
				.bind("telefone");

		BodyView bodyView = new BodyView() {
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fNome);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);

		sexo.setEmptySelectionAllowed(false);
		sexo.setItems(EnumSet.allOf(Sexo.class));
		estado.setEmptySelectionAllowed(false);
		estado.setItems(estadoService.list());
		estado.setItemCaptionGenerator(o -> o.getSigla());

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

			{
				addFields(id, nome, sexo, dataNascimento, cpf, endereco, numero, complemento, bairro, cidade, estado,
						cep, email, telefone);

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
		List<Paciente> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Paciente> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Paciente paciente = new Paciente();
		if (binder.writeBeanIfValid(paciente)) {
			if (paciente.getId() == null) {
				service.insert(paciente);
			} else {
				service.update(paciente);
			}
			updateGrid();
			view();
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fNome.isEmpty())
			params.put("nome#like", fNome.getValue());
		updateGrid(params);
	}

	private void limpar() {
		fNome.clear();
		updateGrid();
	}
}
