package br.pucminas.pucmed.ui.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Especialidade;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.model.Usuario.TipoUsuario;
import br.pucminas.pucmed.service.EspecialidadeService;
import br.pucminas.pucmed.service.MedicoService;
import br.pucminas.pucmed.service.UsuarioService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.utils.MessageBox;
import br.pucminas.pucmed.utils.Constants;

public class MedicoForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private MedicoService service = BeanGetter.getService(MedicoService.class);
	private UsuarioService usuarioService = BeanGetter.getService(UsuarioService.class);
	private EspecialidadeService especialidadeService = BeanGetter.getService(EspecialidadeService.class);

	private Binder<Medico> binder = new Binder<>(Medico.class);

	private Grid<Medico> grid = new Grid<>(Medico.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");
	private TextField crm = new TextField("CRM");
	private ListSelect<Especialidade> especialidades = new ListSelect<>("Especialidade");

	private ComboBox<Usuario> usuario = new ComboBox<>("Usuário");

	private TextField fNome = new TextField("Nome");
	private TextField fCrm = new TextField("CRM");

	public MedicoForm() {
		super("Cadastro de Medicos");

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
		grid.addColumn(o -> o.getUsuario() == null ? null : o.getUsuario().getNome())//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Usuário");

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

				Button botaoExpedientes = getToolbarArea().addCustomButton("Expedientes");
				botaoExpedientes.setIcon(VaadinIcons.CLOCK);
				botaoExpedientes.addClickListener(e -> abrirExpedientes());

				getFilterArea().addFilters(fNome, fCrm);
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

		especialidades.setItems(especialidadeService.list());
		especialidades.setItemCaptionGenerator(Especialidade::getNome);
		especialidades.setRows(6);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

			{
				addFields(id, nome, crm, especialidades, usuario);

				setSalvarListener(e -> salvar());
				setCancelarListener(e -> view());
			}
		};

		setBodyEdit(bodyEdit);
		setBodyView(bodyView);

		view();
	}

	private void abrirExpedientes() {
		getUI().addWindow(new ExpedienteWindow(new MedicoExpedienteForm(binder.getBean())));
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
		List<Medico> usuarios = service.list();
		grid.setItems(usuarios);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Medico> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
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
		updateGrid(params);
	}

	private void limpar() {
		fNome.clear();
		updateGrid();
	}

	private static class ExpedienteWindow extends Window {
		private static final long serialVersionUID = 6157453624162378876L;

		public ExpedienteWindow(BaseForm baseForm) {
			super("Expedientes");
			center();
			setContent(baseForm);
			setVisible(true);
			setWidth("50%");
			setHeight("50%");
		}
	}
}
