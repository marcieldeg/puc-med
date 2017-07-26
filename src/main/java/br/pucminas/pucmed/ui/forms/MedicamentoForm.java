package br.pucminas.pucmed.ui.forms;

import java.util.EnumSet;
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
import br.pucminas.pucmed.model.Medicamento;
import br.pucminas.pucmed.model.Medicamento.Embalagem;
import br.pucminas.pucmed.service.MedicamentoService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class MedicamentoForm extends BaseForm {
	private MedicamentoService service = BeanGetter.getService(MedicamentoService.class);

	private Binder<Medicamento> binder = new Binder<>(Medicamento.class);

	private Grid<Medicamento> grid = new Grid<>(Medicamento.class);
	private TextField id = new TextField("Código");
	private TextField nomeComercial = new TextField("Nome Comercial");
	private TextField nomeGenerico = new TextField("Nome Genérico");
	private TextField fabricante = new TextField("Fabricante");
	private ComboBox<Embalagem> embalagem = new ComboBox<>("Embalagem");

	private TextField fNomeComercial = new TextField("Nome Comercial");
	private TextField fNomeGenerico = new TextField("Nome Genérico");
	private TextField fFabricante = new TextField("Fabricante");

	public static final String CAPTION = "Cadastro de Medicamentos";

	public MedicamentoForm() {
		super(CAPTION);

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn("nomeComercial").setWidth(Constants.LARGE_FIELD);
		grid.addColumn("nomeGenerico").setWidth(Constants.LARGE_FIELD).setCaption("Nome Genérico");
		grid.addColumn("fabricante").setWidth(Constants.MEDIUM_FIELD);
		grid.addColumn("embalagem").setWidth(Constants.MEDIUM_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Medicamento> medicamento = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(medicamento.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(medicamento.isPresent());
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(1);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(nomeComercial)//
				.asRequired("O campo é obrigatório")//
				.bind("nomeComercial");
		binder.forField(nomeGenerico)//
				.asRequired("O campo é obrigatório")//
				.bind("nomeGenerico");
		binder.forField(fabricante)//
				.asRequired("O campo é obrigatório")//
				.bind("fabricante");
		binder.forField(embalagem)//
				.asRequired("O campo é obrigatório")//
				.bind("embalagem");

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());
				
				fNomeComercial.addValueChangeListener(e -> pesquisar());
				fNomeGenerico.addValueChangeListener(e -> pesquisar());
				fFabricante.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fNomeComercial, fNomeGenerico, fFabricante);
			}
		};

		id.setEnabled(false);
		embalagem.setEmptySelectionAllowed(false);
		embalagem.setItems(EnumSet.allOf(Embalagem.class));

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		nomeComercial.addStyleName(Constants.LARGE_FIELD_STYLE);
		nomeGenerico.addStyleName(Constants.LARGE_FIELD_STYLE);
		fabricante.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		embalagem.addStyleName(Constants.SMALL_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, nomeComercial, nomeGenerico, fabricante, embalagem);

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
		List<Medicamento> medicamentos = service.list();
		grid.setItems(medicamentos);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Medicamento> medicamentos = service.list(params);
		grid.setItems(medicamentos);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Medicamento medicamento = new Medicamento();
		if (binder.writeBeanIfValid(medicamento)) {
			if (medicamento.getId() == null)
				service.insert(medicamento);
			else
				service.update(medicamento);
			updateGrid();
			view();
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fNomeComercial.isEmpty())
			params.put("nomeComercial#like", fNomeComercial.getValue());
		if (!fNomeGenerico.isEmpty())
			params.put("nomeGenerico#like", fNomeGenerico.getValue());
		if (!fFabricante.isEmpty())
			params.put("fabricante#like", fFabricante.getValue());
		updateGrid(params);
	}
}
