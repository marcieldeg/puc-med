package br.pucminas.pucmed.ui.forms;

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
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Atendente;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.model.Usuario.TipoUsuario;
import br.pucminas.pucmed.service.AtendenteService;
import br.pucminas.pucmed.service.UsuarioService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.utils.MessageBox;
import br.pucminas.pucmed.utils.Constants;

public class AtendenteForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private AtendenteService service = BeanGetter.getService(AtendenteService.class);
	private UsuarioService usuarioService = BeanGetter.getService(UsuarioService.class);

	private Binder<Atendente> binder = new Binder<>(Atendente.class);

	private Grid<Atendente> grid = new Grid<>(Atendente.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private ComboBox<Usuario> usuario = new ComboBox<>("Usuário");

	private TextField fNome = new TextField("Nome");

	public AtendenteForm() {
		super("Cadastro de Atendentes");

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn("nome").setWidth(Constants.LARGE_FIELD);
		grid.addColumn(o -> o.getUsuario() == null ? null : o.getUsuario().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Usuário");

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
		binder.forField(usuario)//
				.asRequired("O campo é obrigatório")//
				.bind("usuario");

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
		usuario.setEmptySelectionAllowed(false);
		Map<String, Object> filters = new HashMap<>();
		filters.put("status", Status.ATIVO);
		filters.put("tipoUsuario#ne", TipoUsuario.ADMINISTRADOR);
		usuario.setItems(usuarioService.list(filters));
		usuario.setItemCaptionGenerator(Usuario::getNome);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

			{
				addFields(id, nome, usuario);

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
		List<Atendente> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Atendente> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
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
		updateGrid(params);
	}

	private void limpar() {
		fNome.clear();
		updateGrid();
	}
}
