package br.pucminas.pucmed.ui.forms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Atendimento;
import br.pucminas.pucmed.model.Medicamento;
import br.pucminas.pucmed.model.Receituario;
import br.pucminas.pucmed.reports.ReportsRunner;
import br.pucminas.pucmed.service.MedicamentoService;
import br.pucminas.pucmed.service.ReceituarioService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class ReceituarioForm extends BaseForm {
	private Atendimento atendimento;

	private ReceituarioService service = BeanGetter.getService(ReceituarioService.class);
	private MedicamentoService medicamentoService = BeanGetter.getService(MedicamentoService.class);

	private Binder<Receituario> binder = new Binder<>(Receituario.class);

	private Grid<Receituario> grid = new Grid<>(Receituario.class);
	private TextField id = new TextField("Código");
	private ComboBox<Medicamento> medicamento = new ComboBox<>("Medicamento");
	private TextField quantidade = new TextField("Quantidade");
	private TextArea posologia = new TextArea("Posologia");

	private ComboBox<Medicamento> fMedicamento = new ComboBox<>("Medicamento");

	public static final String CAPTION = "Cadastro de Atendimentos";

	public ReceituarioForm(Atendimento atendimento) {
		super(CAPTION);

		this.atendimento = atendimento;

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn(o -> o.getMedicamento() == null ? null : o.getMedicamento().getNomeComercial())//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Medicamento");
		grid.addColumn("quantidade").setWidth(Constants.SMALL_FIELD);
		grid.addColumn("posologia").setWidth(Constants.LARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Receituario> receituario = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(receituario.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(receituario.isPresent());
		});

		grid.setSizeFull();

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(medicamento)//
				.asRequired("O campo é obrigatório")//
				.bind("medicamento");
		binder.forField(quantidade)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.asRequired("O campo é obrigatório")//
				.bind("quantidade");
		binder.forField(posologia)//
				.asRequired("O campo é obrigatório")//
				.bind("posologia");

		List<Medicamento> medicamentos = medicamentoService.list();

		fMedicamento.setItems(medicamentos);
		fMedicamento.setItemCaptionGenerator(Medicamento::getNomeComercial);

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				Button botaoImprimir = getToolbarArea().addCustomButton("Imprimir");
				botaoImprimir.setIcon(VaadinIcons.PRINT);
				botaoImprimir.setEnabled(true);

				BrowserWindowOpener browserWindowOpener = new BrowserWindowOpener(//
						new StreamResource(//
								new StreamSource() {
									@Override
									public InputStream getStream() {
										return new ByteArrayInputStream(
												new ReportsRunner().runReceituario(atendimento.getId()));
									}
								}, "receituário_" + atendimento.getId() + ".pdf") {
							{
								setMIMEType("application/pdf");
							}
						});
				browserWindowOpener.extend(botaoImprimir);

				fMedicamento.addValueChangeListener(e -> pesquisar());
				getFilterArea().addFilters(fMedicamento);
			}
		};

		id.setEnabled(false);
		medicamento.setEmptySelectionAllowed(false);
		medicamento.setItems(medicamentos);
		medicamento.setItemCaptionGenerator(Medicamento::getNomeComercial);

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		medicamento.addStyleName(Constants.LARGE_FIELD_STYLE);
		quantidade.addStyleName(Constants.SMALL_FIELD_STYLE);
		posologia.addStyleName(Constants.XLARGE_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, medicamento, quantidade, posologia);

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
		updateGrid(new HashMap<>());
	}

	private void updateGrid(Map<String, Object> params) {
		params.put("atendimento", atendimento);
		List<Receituario> usuarios = service.list(params);
		grid.setItems(usuarios);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Receituario receituario = new Receituario();
		if (binder.writeBeanIfValid(receituario)) {
			if (receituario.getId() == null) {
				receituario.setAtendimento(atendimento);
				service.insert(receituario);
			} else {
				service.update(receituario);
			}
			updateGrid();
			view();
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fMedicamento.isEmpty())
			params.put("medicamento", fMedicamento.getValue());
		updateGrid(params);
	}
}
