package br.pucminas.pucmed.ui.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Especialidade;
import br.pucminas.pucmed.service.EspecialidadeService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class EspecialidadeForm extends BaseForm {
	private EspecialidadeService service = BeanGetter.getService(EspecialidadeService.class);

	private Binder<Especialidade> binder = new Binder<>(Especialidade.class);

	private Grid<Especialidade> grid = new Grid<>(Especialidade.class);
	private TextField id = new TextField("Código");
	private TextField nome = new TextField("Nome");

	private TextField fNome = new TextField("Nome");

	public static final String CAPTION = "Cadastro de Especialidades";

	public EspecialidadeForm() {
		super(CAPTION);

		updateGrid();
		grid.setColumns("id", "nome");
		grid.getColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.getColumn("nome").setWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Especialidade> especialidade = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(especialidade.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(especialidade.isPresent());
		});

		grid.setSizeFull();

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(nome)//
				.asRequired("O campo é obrigatório")//
				.bind("nome");

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
		
		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		nome.addStyleName(Constants.LARGE_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, nome);

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
		List<Especialidade> especialidades = service.list();
		grid.setItems(especialidades);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Especialidade> especialidades = service.list(params);
		grid.setItems(especialidades);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Especialidade especialidade = new Especialidade();
		if (binder.writeBeanIfValid(especialidade)) {
			if (especialidade.getId() == null)
				service.insert(especialidade);
			else
				service.update(especialidade);
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
