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
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Laboratorio;
import br.pucminas.pucmed.service.LaboratorioService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

@SuppressWarnings("serial")
public class LaboratorioForm extends BaseForm {
	private LaboratorioService service = BeanGetter.getService(LaboratorioService.class);

	private Binder<Laboratorio> binder = new Binder<>(Laboratorio.class);

	private Grid<Laboratorio> grid = new Grid<>(Laboratorio.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private TextField email = new TextField("E-mail");
	private TextField login = new TextField("Usuário");
	private PasswordField senha = new PasswordField("Senha");
	private ComboBox<Status> status = new ComboBox<>("Status");

	private TextField fNome = new TextField("Nome");
	private TextField fLogin = new TextField("Login");

	public static final String CAPTION = "Cadastro de Laboratórios";

	public LaboratorioForm() {
		super(CAPTION);

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id")//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD);
		grid.addColumn("nome")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD);
		grid.addColumn("email")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD)//
				.setCaption("E-mail");
		grid.addColumn("login")//
				.setMinimumWidth(Constants.MEDIUM_FIELD)//
				.setMaximumWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Laboratorio> Laboratorio = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(Laboratorio.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(Laboratorio.isPresent());
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(1);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(nome)//
				.asRequired("O campo é obrigatório")//
				.bind("nome");
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

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				fNome.addValueChangeListener(e -> pesquisar());
				fLogin.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fNome, fLogin);
			}
		};

		id.setEnabled(false);
		status.setEmptySelectionAllowed(false);
		status.setItems(EnumSet.allOf(Status.class));

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		nome.addStyleName(Constants.LARGE_FIELD_STYLE);
		email.addStyleName(Constants.LARGE_FIELD_STYLE);
		login.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		senha.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		status.addStyleName(Constants.SMALL_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, nome, email, login, senha, status);

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
			status.setEnabled(true);
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
		List<Laboratorio> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Laboratorio> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		status.setSelectedItem(Status.ATIVO);
		status.setEnabled(false);
		edit();
	}

	private void salvar() {
		Laboratorio Laboratorio = new Laboratorio();
		if (binder.writeBeanIfValid(Laboratorio)) {
			try {
				if (Laboratorio.getId() == null) {
					service.insert(Laboratorio);
				} else {
					service.update(Laboratorio);
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
		if (!fLogin.isEmpty())
			params.put("login#like", fLogin.getValue());
		updateGrid(params);
	}
}
