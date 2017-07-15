package br.pucminas.pucmed.ui.forms;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import br.pucminas.pucmed.model.Atendente;
import br.pucminas.pucmed.service.AtendenteService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.utils.Constants;

public class AtendenteForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private AtendenteService service = BeanGetter.getService(AtendenteService.class);

	private Binder<Atendente> binder = new Binder<>(Atendente.class);

	private Grid<Atendente> grid = new Grid<>(Atendente.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private TextField email = new TextField("E-mail");
	private TextField login = new TextField("Login");
	private PasswordField senha = new PasswordField("Senha");
	private ComboBox<Status> status = new ComboBox<>("Status");

	private TextField fNome = new TextField("Nome");
	private TextField fLogin = new TextField("Login");

	public AtendenteForm() {
		super();

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn("nome").setWidth(Constants.LARGE_FIELD);
		grid.addColumn("email").setWidth(Constants.LARGE_FIELD).setCaption("E-mail");
		grid.addColumn("login").setWidth(Constants.MEDIUM_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Atendente> atendente = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(atendente.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(atendente.isPresent());
		});

		grid.setSizeFull();

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
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fNome, fLogin);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);
		status.setEmptySelectionAllowed(false);
		status.setItems(EnumSet.allOf(Status.class));

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

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
						service.delete(grid.asSingleSelect().getValue());
						updateGrid();
					});
		}
	}

	private void updateGrid() {
		List<Atendente> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Atendente> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		status.setSelectedItem(Status.ATIVO);
		status.setEnabled(false);
		edit();
	}

	private void salvar() {
		Atendente atendente = new Atendente();
		if (binder.writeBeanIfValid(atendente)) {
			if (atendente.getId() == null) {
				service.insert(atendente);
			} else {
				service.update(atendente);
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
		if (!fLogin.isEmpty())
			params.put("login#like", fLogin.getValue());
		updateGrid(params);
	}

	private void limpar() {
		fNome.clear();
		fLogin.clear();
		updateGrid();
	}
}
