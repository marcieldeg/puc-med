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

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import br.com.caelum.stella.validation.CPFValidator;
import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Estado;
import br.pucminas.pucmed.model.Paciente;
import br.pucminas.pucmed.model.Paciente.Sexo;
import br.pucminas.pucmed.service.EstadoService;
import br.pucminas.pucmed.service.PacienteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

@SuppressWarnings("serial")
public class PacienteForm extends BaseForm {
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
	private TextField cidade = new TextField("Cidade");
	private ComboBox<Estado> estado = new ComboBox<>("Estado");
	private TextField cep = new TextField("CEP");
	private TextField email = new TextField("E-mail");
	private TextField telefone = new TextField("Telefone");

	private TextField fNome = new TextField("Nome");
	private ComboBox<Estado> fEstado = new ComboBox<>("Estado");

	public static final String CAPTION = "Cadastro de Pacientes";

	public PacienteForm() {
		super(CAPTION);

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id")//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD);
		grid.addColumn("nome")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD);
		grid.addColumn(o -> o.getSexo().getValue())//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD)//
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
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD)//
				.setCaption("Idade");
		grid.addColumn("cpf")//
				.setMinimumWidth(Constants.SMALL_FIELD)//
				.setMaximumWidth(Constants.MEDIUM_FIELD)//
				.setCaption("CPF");
		grid.addColumn("endereco")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("Endereço");
		grid.addColumn("numero")//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD)//
				.setCaption("Nº");
		grid.addColumn("complemento")//
				.setMinimumWidth(Constants.SMALL_FIELD)//
				.setMaximumWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Complemento");
		grid.addColumn("bairro")//
				.setMinimumWidth(Constants.SMALL_FIELD)//
				.setMaximumWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Bairro");
		grid.addColumn("cidade")//
				.setMinimumWidth(Constants.SMALL_FIELD)//
				.setMaximumWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Cidade");
		grid.addColumn(o -> o.getEstado() == null ? null : o.getEstado().getNome())//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("Estado");
		grid.addColumn("cep")//
				.setWidth(Constants.SMALL_FIELD)//
				.setCaption("CEP");
		grid.addColumn("email")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("E-mail");
		grid.addColumn("telefone")//
				.setMinimumWidth(Constants.SMALL_FIELD)//
				.setMaximumWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Telefone");

		grid.addSelectionListener(e -> {
			Optional<Paciente> paciente = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(paciente.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(paciente.isPresent());
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(1);

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
				.withValidator(new AbstractValidator<String>("CPF Inválido") {
					private final CPFValidator cpfValidator = new CPFValidator();

					@Override
					public ValidationResult apply(String value, ValueContext context) {
						return toResult(value, cpfValidator.invalidMessagesFor(value).isEmpty());
					}
				})//
				.bind("cpf");
		binder.forField(endereco)//
				.asRequired("O campo é obrigatório")//
				.bind("endereco");
		binder.forField(numero)//
				.withNullRepresentation("")//
				.withConverter(new StringToLongConverter("Número Inválido"))//
				.bind("numero");
		binder.forField(complemento)//
				.withNullRepresentation("")//
				.bind("complemento");
		binder.forField(bairro)//
				.withNullRepresentation("")//
				.bind("bairro");
		binder.forField(cidade)//
				.withNullRepresentation("")//
				.bind("cidade");
		binder.forField(estado)//
				.bind("estado");
		binder.forField(cep)//
				.withNullRepresentation("")//
				.bind("cep");
		binder.forField(email)//
				.withNullRepresentation("")//
				.withValidator(new EmailValidator("E-mail inválido"))//
				.bind("email");
		binder.forField(telefone)//
				.withNullRepresentation("")//
				.bind("telefone");

		fEstado.setItems(estadoService.list());
		fEstado.setItemCaptionGenerator(Estado::getSigla);

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				fNome.addValueChangeListener(e -> pesquisar());
				fEstado.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fNome, fEstado);
			}
		};

		id.setEnabled(false);

		sexo.setEmptySelectionAllowed(false);
		sexo.setItems(EnumSet.allOf(Sexo.class));
		estado.setEmptySelectionAllowed(false);
		estado.setItems(estadoService.list());
		estado.setItemCaptionGenerator(Estado::getSigla);

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		nome.addStyleName(Constants.LARGE_FIELD_STYLE);
		sexo.addStyleName(Constants.SMALL_FIELD_STYLE);
		dataNascimento.addStyleName(Constants.SMALL_FIELD_STYLE);
		cpf.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		cpf.setMaxLength(11);
		endereco.addStyleName(Constants.LARGE_FIELD_STYLE);
		numero.addStyleName(Constants.SMALL_FIELD_STYLE);
		complemento.addStyleName(Constants.LARGE_FIELD_STYLE);
		bairro.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		cidade.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		estado.addStyleName(Constants.SMALL_FIELD_STYLE);
		cep.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		email.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		telefone.addStyleName(Constants.MEDIUM_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
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
			try {
				if (paciente.getId() == null) {
					service.insert(paciente);
				} else {
					service.update(paciente);
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
		if (!fNome.isEmpty())
			params.put("nome#like", fNome.getValue());
		if (!fEstado.isEmpty())
			params.put("estado", fEstado.getValue());
		updateGrid(params);
	}
}
