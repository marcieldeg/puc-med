package br.pucminas.pucmed.ui.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.service.UsuarioService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class UsuarioForm extends BaseForm {
	private UsuarioService service = BeanGetter.getService(UsuarioService.class);

	private Binder<Usuario> binder = new Binder<>(Usuario.class);

	private Grid<Usuario> grid = new Grid<>(Usuario.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private PasswordField senha = new PasswordField("Senha");
	private PasswordField confSenha = new PasswordField("Confirmar senha");
	private TextField email = new TextField("E-Mail");

	private TextField fNome = new TextField("Nome");

	public UsuarioForm() {
		super();

		updateGrid();
		grid.setColumns("id", "nome", "email");
		grid.getColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.getColumn("nome").setWidth(Constants.LARGE_FIELD);
		grid.getColumn("email").setWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Usuario> usuario = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(usuario.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(usuario.isPresent());
		});

		grid.setSizeFull();

		senha.setMaxLength(20);
		confSenha.setMaxLength(20);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(nome)//
				.asRequired("O campo é obrigatório")//
				.bind("nome");
		binder.forField(senha)//
				.withValidator(new StringLengthValidator("A senha deve conter entre 5 e 20 caracteres", 5, 20))//
				.asRequired("O campo é obrigatório")//
				.bind("senha");
		binder.forField(email)//
				.withValidator(new EmailValidator("E-mail inválido"))//
				.asRequired("O campo é obrigatório")//
				.bind("email");

		binder.withValidator(//
				(s, v) -> {
					if (!s.getSenha().equals(confSenha.getValue())) {
						Notification.show("As senhas digitadas não conferem", Type.ERROR);
						return ValidationResult.error("As senhas digitadas não conferem");
					} else
						return ValidationResult.ok();
				});

		BodyView bodyView = new BodyView() {
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

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, nome, senha, confSenha, email);

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
		List<Usuario> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Usuario> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Usuario usuario = new Usuario();
		if (binder.writeBeanIfValid(usuario)) {
			if (usuario.getId() == null) {
				usuario.setStatus(Status.ATIVO);
				service.insert(usuario);
			} else {
				service.update(usuario);
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
