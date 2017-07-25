package br.pucminas.pucmed.ui.forms;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Especialidade;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.service.EspecialidadeService;
import br.pucminas.pucmed.service.MedicoService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.SubWindow;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class MedicoForm extends BaseForm {
	private MedicoService service = BeanGetter.getService(MedicoService.class);
	private EspecialidadeService especialidadeService = BeanGetter.getService(EspecialidadeService.class);

	private Binder<Medico> binder = new Binder<>(Medico.class);

	private Grid<Medico> grid = new Grid<>(Medico.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private TextField crm = new TextField("CRM");
	private ListSelect<Especialidade> especialidades = new ListSelect<>("Especialidade");
	private TextField email = new TextField("E-mail");
	private TextField login = new TextField("Login");
	private PasswordField senha = new PasswordField("Senha");
	private ComboBox<Status> status = new ComboBox<>("Status");

	private TextField fNome = new TextField("Nome");
	private TextField fCrm = new TextField("CRM");
	private ComboBox<Especialidade> fEspecialidade = new ComboBox<>("Especialidade");

	public static final String CAPTION = "Cadastro de Médicos";

	public MedicoForm() {
		super(CAPTION);

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn("nome").setWidth(Constants.LARGE_FIELD);
		grid.addColumn("crm")//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("CRM");
		grid.addColumn(o -> o.getEspecialidades() == null ? null
				: o.getEspecialidades().stream().map(p -> p.getNome()).collect(Collectors.joining(", ")))//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Especialidades");
		grid.addColumn("email").setWidth(Constants.LARGE_FIELD).setCaption("E-mail");
		grid.addColumn("login").setWidth(Constants.MEDIUM_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Medico> medico = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(medico.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(medico.isPresent());
			getBodyView().getToolbarArea().getCustomButton("Expedientes").setEnabled(medico.isPresent());
		});

		grid.setSizeFull();

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(nome)//
				.asRequired("O campo é obrigatório")//
				.bind("nome");
		binder.forField(crm)//
				.withConverter(new StringToLongConverter("Número Inválido"))//
				.asRequired("O campo é obrigatório")//
				.bind("crm");
		binder.forField(especialidades)//
				.asRequired("O campo é obrigatório")//
				.bind("especialidades");
		binder.forField(email)//
				.withValidator(new EmailValidator("E-mail inválido"))//
				.asRequired("O campo é obrigatório")//
				.bind("email");
		binder.forField(login)//
				.withValidator(new StringLengthValidator("O login deve ter entre 5 e 20 caracteres", 5, 20))//
				.asRequired("O campo é obrigatório")//
				.bind("login");
		binder.forField(senha)//
				.withValidator(new StringLengthValidator("A senha deve ter entre 5 e 20 caracteres", 5, 20))//
				.asRequired("O campo é obrigatório")//
				.bind("senha");
		binder.forField(status)//
				.asRequired("O campo é obrigatório")//
				.bind("status");

		fEspecialidade.setItems(especialidadeService.list());
		fEspecialidade.setItemCaptionGenerator(Especialidade::getNome);

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				Button botaoExpedientes = getToolbarArea().addCustomButton("Expedientes");
				botaoExpedientes.setIcon(VaadinIcons.CLOCK);
				botaoExpedientes.addClickListener(e -> abrirExpedientes());

				fNome.addValueChangeListener(e -> pesquisar());
				fCrm.addValueChangeListener(e -> pesquisar());
				fEspecialidade.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fNome, fCrm, fEspecialidade);
			}
		};

		id.setEnabled(false);
		status.setEmptySelectionAllowed(false);
		status.setItems(EnumSet.allOf(Status.class));
		especialidades.setItems(especialidadeService.list());
		especialidades.setItemCaptionGenerator(Especialidade::getNome);
		especialidades.setRows(6);
		especialidades.setSizeFull();

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		nome.addStyleName(Constants.LARGE_FIELD_STYLE);
		crm.addStyleName(Constants.SMALL_FIELD_STYLE);
		especialidades.addStyleName(Constants.SMALL_FIELD_STYLE);
		email.addStyleName(Constants.LARGE_FIELD_STYLE);
		login.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		senha.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		status.addStyleName(Constants.SMALL_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, nome, crm, especialidades, email, login, senha, status);

				setSalvarListener(e -> salvar());
				setCancelarListener(e -> view());
			}
		};

		setBodyEdit(bodyEdit);
		setBodyView(bodyView);

		view();
	}

	private void abrirExpedientes() {
		SubWindow subWindow = new SubWindow(" Expedientes", new MedicoExpedienteForm(grid.asSingleSelect().getValue()));
		subWindow.addCloseListener(o -> getUI().removeWindow(subWindow));
		subWindow.setIcon(VaadinIcons.CLOCK);
		getUI().addWindow(subWindow);
	}

	private void editar() {
		if (!grid.asSingleSelect().isEmpty()) {
			binder.setBean(grid.asSingleSelect().getValue());
			status.setEnabled(true);
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
		List<Medico> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Medico> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		status.setSelectedItem(Status.ATIVO);
		status.setEnabled(false);
		edit();
	}

	private void salvar() {
		Medico medico = new Medico();
		if (binder.writeBeanIfValid(medico)) {
			if (medico.getId() == null) {
				service.insert(medico);
			} else {
				service.update(medico);
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
		if (!fCrm.isEmpty())
			params.put("crm", fNome.getValue());
		if (!fEspecialidade.isEmpty()) {
			params.put("especialidades#exists", fEspecialidade.getValue().getId());
		}
		updateGrid(params);
	}
}
