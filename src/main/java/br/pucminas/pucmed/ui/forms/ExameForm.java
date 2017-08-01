package br.pucminas.pucmed.ui.forms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Atendimento;
import br.pucminas.pucmed.model.Exame;
import br.pucminas.pucmed.model.Paciente;
import br.pucminas.pucmed.model.TipoExame;
import br.pucminas.pucmed.reports.ReportsRunner;
import br.pucminas.pucmed.service.ExameService;
import br.pucminas.pucmed.service.PacienteService;
import br.pucminas.pucmed.service.TipoExameService;
import br.pucminas.pucmed.ui.BaseForm;
import br.pucminas.pucmed.ui.BodyEdit;
import br.pucminas.pucmed.ui.BodyView;
import br.pucminas.pucmed.ui.extra.MessageBox;
import br.pucminas.pucmed.ui.extra.Notification;
import br.pucminas.pucmed.ui.extra.Notification.Type;
import br.pucminas.pucmed.utils.Constants;
import br.pucminas.pucmed.utils.Utils;

@SuppressWarnings("serial")
public class ExameForm extends BaseForm {
	private Atendimento atendimento;

	private ExameService service = BeanGetter.getService(ExameService.class);
	private TipoExameService tipoExameService = BeanGetter.getService(TipoExameService.class);
	private PacienteService pacienteService = BeanGetter.getService(PacienteService.class);

	private Binder<Exame> binder = new Binder<>(Exame.class);

	private Grid<Exame> grid = new Grid<>(Exame.class);
	private TextField id = new TextField("Código");
	private ComboBox<TipoExame> tipoExame = new ComboBox<>("Tipo de Exame");
	private DateTimeField dataRealizacao = new DateTimeField("Data de Realização");
	private TextArea resultado = new TextArea("Resultado");

	private ComboBox<Paciente> fPaciente = new ComboBox<>("Paciente");
	private ComboBox<TipoExame> fTipoExame = new ComboBox<>("Tipo de Exame");
	private DateField fDataRealizacao = new DateField("Data de Realização");
	private ComboBox<Situacao> fStatus = new ComboBox<>("Situação", EnumSet.allOf(Situacao.class));

	private boolean abertoDoMenu = false;

	public static final String CAPTION = "Cadastro de Exames";

	public ExameForm() {
		this(null);
	}

	public ExameForm(Atendimento atendimento) {
		super(CAPTION);

		this.atendimento = atendimento;
		abertoDoMenu = this.atendimento == null;

		if (abertoDoMenu)
			fStatus.setSelectedItem(Situacao.NAO_REALIZADOS);

		updateGrid();
		grid.removeAllColumns();
		grid.addColumn("id")//
				.setMinimumWidth(Constants.XSMALL_FIELD)//
				.setMaximumWidth(Constants.SMALL_FIELD);
		grid.addColumn(//
				o -> {
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					return format.format(o.getAtendimento().getData());
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
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					return format.format(o.getDataRealizacao());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Data de Realização");
		grid.addColumn("resultado")//
				.setMinimumWidth(Constants.LARGE_FIELD)//
				.setMaximumWidth(Constants.XLARGE_FIELD);

		grid.addSelectionListener(e -> {
			Optional<Exame> exame = e.getFirstSelectedItem();
			getBodyView().getToolbarArea().setEditarEnabled(exame.isPresent());
			getBodyView().getToolbarArea().setExcluirEnabled(exame.isPresent() && !abertoDoMenu);
			getBodyView().getToolbarArea().getCustomButton("Imprimir")
					.setEnabled(exame.isPresent() && exame.get().getDataRealizacao() != null);
		});

		grid.setSizeFull();
		grid.setFrozenColumnCount(1);

		binder.forField(id)//
				.withConverter(new StringToLongConverter("Código Inválido"))//
				.bind("id");
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

		if (abertoDoMenu) {
			fPaciente.setItems(pacienteService.list());
			fPaciente.setItemCaptionGenerator(Paciente::getNome);
		}

		BodyView bodyView = new BodyView() {
			{
				setBody(grid);

				getToolbarArea().setAdicionarListener(e -> novo());
				getToolbarArea().setEditarListener(e -> editar());
				getToolbarArea().setExcluirListener(e -> excluir());

				Button botaoImprimir = getToolbarArea().addCustomButton("Imprimir");
				botaoImprimir.setIcon(VaadinIcons.PRINT);

				BrowserWindowOpener browserWindowOpener = new BrowserWindowOpener(//
						new StreamResource(//
								new StreamSource() {
									@Override
									public InputStream getStream() {
										return new ByteArrayInputStream(
												new ReportsRunner().runExame(grid.asSingleSelect().getValue().getId()));
									}
								}, "exame.pdf") {
							{
								setMIMEType("application/pdf");
							}
						});
				browserWindowOpener.extend(botaoImprimir);

				fTipoExame.addValueChangeListener(e -> pesquisar());
				fDataRealizacao.addValueChangeListener(e -> pesquisar());
				fStatus.addValueChangeListener(e -> pesquisar());

				if (abertoDoMenu) {
					fPaciente.addValueChangeListener(e -> pesquisar());
					getToolbarArea().setAdicionarEnabled(false);
					getToolbarArea().setExcluirEnabled(false);
					getFilterArea().addFilters(fPaciente);
				}
				getFilterArea().addFilters(fTipoExame, fDataRealizacao, fStatus);
			}
		};

		id.setEnabled(false);
		tipoExame.setItems(tipoExameService.list());
		tipoExame.setItemCaptionGenerator(TipoExame::getNome);
		tipoExame.setEmptySelectionAllowed(false);

		id.addStyleName(Constants.SMALL_FIELD_STYLE);
		tipoExame.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		dataRealizacao.addStyleName(Constants.MEDIUM_FIELD_STYLE);
		resultado.addStyleName(Constants.XLARGE_FIELD_STYLE);

		BodyEdit bodyEdit = new BodyEdit() {
			{
				addFields(id, tipoExame, dataRealizacao, resultado);

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
			Exame e = grid.asSingleSelect().getValue();
			atendimento = e.getAtendimento();

			if (e.getDataRealizacao() != null) {
				Notification.show("Esse exame já foi realizado e não pode ser alterado", Type.ERROR);
				return;
			}

			binder.setBean(e);
			edit();
		}
	}

	private void excluir() {
		if (!grid.asSingleSelect().isEmpty()) {
			Exame e = grid.asSingleSelect().getValue();

			if (e.getDataRealizacao() != null) {
				Notification.show("Esse exame já foi realizado e não pode ser excluído", Type.ERROR);
				return;
			}

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
		updateGrid(new HashMap<>());
	}

	private void updateGrid(Map<String, Object> params) {
		if (!abertoDoMenu)
			params.put("atendimento", atendimento);
		List<Exame> exames = service.list(params);
		grid.setItems(exames);
	}

	private void novo() {
		binder.setBean(null);
		edit();
	}

	private void salvar() {
		Exame exame = new Exame();
		exame.setAtendimento(atendimento);
		if (binder.writeBeanIfValid(exame)) {
			try {
				if (exame.getId() == null) {
					service.insert(exame);
				} else
					service.update(exame);
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
		if (!fTipoExame.isEmpty())
			params.put("tipoExame", fTipoExame.getValue());

		if (!fDataRealizacao.isEmpty()) {
			params.put("dataRealizacao#ge", Utils.convertLocalDateToDate(fDataRealizacao.getValue()));
			params.put("dataRealizacao#lt", Utils.convertLocalDateToDate(fDataRealizacao.getValue().plusDays(1)));
		}

		if (!fStatus.isEmpty())
			switch (fStatus.getValue()) {
			case NAO_REALIZADOS:
				params.put("dataRealizacao#isnull", null);
				break;
			case REALIZADOS:
				params.put("dataRealizacao#isnotnull", null);
				break;
			}

		updateGrid(params);
	}

	@Override
	protected void edit() {
		tipoExame.setEnabled(!abertoDoMenu);
		dataRealizacao.setEnabled(abertoDoMenu);
		resultado.setEnabled(abertoDoMenu);
		super.edit();
	}

	private static enum Situacao {
		REALIZADOS, NAO_REALIZADOS;

		@Override
		public String toString() {
			switch (this) {
			case NAO_REALIZADOS:
				return "Não Realizados";
			case REALIZADOS:
				return "Realizados";
			default:
				return "";
			}
		}
	}
}
