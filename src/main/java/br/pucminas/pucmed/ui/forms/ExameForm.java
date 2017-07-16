package br.pucminas.pucmed.ui.forms;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Atendimento;
import br.pucminas.pucmed.model.Exame;
import br.pucminas.pucmed.model.TipoExame;
import br.pucminas.pucmed.service.ExameService;
import br.pucminas.pucmed.service.TipoExameService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

public class ExameForm extends BaseForm {
	private static final long serialVersionUID = 3796349348214384355L;

	private Atendimento atendimento;

	private ExameService service = BeanGetter.getService(ExameService.class);
	private TipoExameService tipoExameService = BeanGetter.getService(TipoExameService.class);

	private Binder<Exame> binder = new Binder<>(Exame.class);

	private Grid<Exame> grid = new Grid<>(Exame.class);
	private TextField id = new TextField("Código");
	private DateTimeField dataSolicitacao = new DateTimeField("Data de Solicitação");
	private ComboBox<TipoExame> tipoExame = new ComboBox<>("Tipo de Exame");
	private DateTimeField dataRealizacao = new DateTimeField("Data de Realização");
	private TextArea resultado = new TextArea("Resultado");

	private DateField fDataSolicitacao = new DateField("Data de Solicitação");
	private ComboBox<TipoExame> fTipoExame = new ComboBox<>("Tipo de Exame");
	private DateField fDataRealizacao = new DateField("Data de Realização");

	public ExameForm(Atendimento atendimento) {
		super();

		this.atendimento = atendimento;

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id").setWidth(Constants.SMALL_FIELD);
		grid.addColumn(//
				o -> {
					if (o.getDataSolicitacao() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
					return format.format(o.getDataSolicitacao());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Data de Solicitação");
		grid.addColumn(o -> o.getTipoExame() == null ? null : o.getTipoExame().getNome())//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Tipo de Exame");
		grid.addColumn(//
				o -> {
					if (o.getDataRealizacao() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
					return format.format(o.getDataRealizacao());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Data de Realização");
		grid.addColumn("resultado").setWidth(Constants.XLARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Exame> exame = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(exame.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(exame.isPresent());
		});

		grid.setSizeFull();

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
		binder.forField(dataSolicitacao)//
				.withConverter(new LocalDateTimeToDateConverter(Constants.ZONE_OFFSET))//
				.asRequired("O campo é obrigatório")//
				.bind("dataSolicitacao");
		binder.forField(tipoExame)//
				.asRequired("O campo é obrigatório")//
				.bind("tipoExame");
		binder.forField(dataRealizacao)//
				.withConverter(new LocalDateTimeToDateConverter(Constants.ZONE_OFFSET))//
				.bind("dataRealizacao");
		binder.forField(resultado)//
				.bind("resultado");

		fTipoExame.setItems(tipoExameService.list());
		fTipoExame.setItemCaptionGenerator(TipoExame::getNome);
		fTipoExame.setEmptySelectionAllowed(false);

		BodyView bodyView = new BodyView() {
			private static final long serialVersionUID = -4336915723509556999L;

			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				getFilterArea().addFilters(fTipoExame, fDataSolicitacao, fDataRealizacao);
				getFilterArea().setPesquisarListener(e -> pesquisar());
				getFilterArea().setLimparListener(e -> limpar());
			}
		};

		id.setEnabled(false);

		tipoExame.setItems(tipoExameService.list());
		tipoExame.setItemCaptionGenerator(TipoExame::getNome);
		tipoExame.setEmptySelectionAllowed(false);

		BodyEdit bodyEdit = new BodyEdit() {
			private static final long serialVersionUID = 6951503876938584530L;

			{
				addFields(id, dataSolicitacao, tipoExame, dataRealizacao, resultado);

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
		List<Exame> exames = service.list();
		grid.setItems(exames);
	}

	private void updateGrid(Map<String, Object> params) {
		List<Exame> exames = service.list(params);
		grid.setItems(exames);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Exame exame = new Exame();
		if (binder.writeBeanIfValid(exame)) {
			if (exame.getId() == null) {
				if (atendimento == null)
					return;
				exame.setAtendimento(atendimento);
				service.insert(exame);
			} else
				service.update(exame);
			updateGrid();
			view();
		} else {
			binder.validate();
		}
	}

	private void pesquisar() {
		Map<String, Object> params = new HashMap<>();
		if (!fDataSolicitacao.isEmpty()) {
			params.put("dataSolicitacao#ge", Utils.convertLocalDateToDate(fDataSolicitacao.getValue()));
			params.put("dataSolicitacao#lt", Utils.convertLocalDateToDate(fDataSolicitacao.getValue().plusDays(1)));
		}
		if (!fTipoExame.isEmpty()) {
			params.put("tipoExame", fTipoExame.getValue());
		}
		if (!fDataRealizacao.isEmpty()) {
			params.put("dataRealizacao#ge", Utils.convertLocalDateToDate(fDataRealizacao.getValue()));
			params.put("dataRealizacao#lt", Utils.convertLocalDateToDate(fDataRealizacao.getValue().plusDays(1)));
		}
		updateGrid(params);
	}

	private void limpar() {
		fDataSolicitacao.clear();
		fTipoExame.clear();
		fDataRealizacao.clear();
		updateGrid();
	}
}
