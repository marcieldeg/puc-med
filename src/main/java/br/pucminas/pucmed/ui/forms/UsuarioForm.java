package br.pucminas.pucmed.ui.forms;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.model.Usuario.TipoUsuario;
import br.pucminas.pucmed.service.UsuarioService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.utils.MessageBox;
import br.pucminas.pucmed.ui.utils.Notification;
import br.pucminas.pucmed.ui.utils.Notification.Type;
import br.pucminas.pucmed.utils.Constants;

public class UsuarioForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private UsuarioService service = BeanGetter.getService(UsuarioService.class);

	private Binder<Usuario> binder = new Binder<>(Usuario.class);

	private Grid<Usuario> grid = new Grid<>(Usuario.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private PasswordField senha = new PasswordField("Senha");
	private PasswordField confSenha = new PasswordField("Confirmar senha");
	private TextField email = new TextField("E-Mail");
	private ComboBox<TipoUsuario> tipoUsuario = new ComboBox<>("Tipo");

	private TextField fNome = new TextField("Nome");
	private ComboBox<TipoUsuario> fTipoUsuario = new ComboBox<>("Tipo");

	public UsuarioForm() {
		super("Cadastro de Usuários");

		updateGrid();
		grid.setColumns("id", "nome", "email", "tipoUsuario");
		grid.getColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.getColumn("nome").setWidth(Constants.LARGE_FIELD);
		grid.getColumn("email").setWidth(Constants.LARGE_FIELD);
		grid.getColumn("tipoUsuario").setWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Usuario> usuario = e.getFirstSelectedItem();
			boolean enabled = false;
			if (usuario.isPresent())
				enabled = !TipoUsuario.ADMINISTRADOR.equals(usuario.get().getTipoUsuario());
			getBodyView().getToolbarArea().setEditarEnabled(enabled);
			getBodyView().getToolbarArea().setExcluirEnabled(enabled);
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
		binder.forField(tipoUsuario)//
				.asRequired("O campo é obrigatório")//
				.bind("tipoUsuario");

		binder.withValidator(new Validator<Usuario>() {
			private static final long serialVersionUID = 6277993876834752213L;

			@Override
			public ValidationResult apply(Usuario s, ValueContext valueContext) {
				if (!s.getSenha().equals(confSenha.getValue())) {
					Notification.show("As senhas digitadas não conferem", Type.ERROR);
					return ValidationResult.error("As senhas digitadas não conferem");
				} else
					return ValidationResult.ok();
			}
		});

		tipoUsuario.setEmptySelectionAllowed(false);
		tipoUsuario.setItems(TipoUsuario.ATENDENTE, TipoUsuario.MEDICO);
		fTipoUsuario.setEmptySelectionAllowed(false);
		fTipoUsuario.setItems(EnumSet.allOf(TipoUsuario.class));

		BodyView bodyView = new BodyView() {
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fNome, fTipoUsuario);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

			{
				addFields(id, nome, senha, confSenha, email, tipoUsuario);

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
		if (!fTipoUsuario.isEmpty())
			params.put("tipoUsuario", fTipoUsuario.getValue());
		updateGrid(params);
	}

	private void limpar() {
		fNome.clear();
		fTipoUsuario.clear();
		updateGrid();
	}
}
