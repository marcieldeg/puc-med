package br.pucminas.pucmed.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import br.pucminas.pucmed.authentication.UserSession;
import br.pucminas.pucmed.bean.BeanGetter;
import br.pucminas.pucmed.model.Agenda;
import br.pucminas.pucmed.model.Recepcionista;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.service.AgendaService;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class WelcomeLayout extends VerticalLayout {
	private final Grid<Agenda> grid = new Grid<>();

	private final AgendaService service = BeanGetter.getService(AgendaService.class);

	private Usuario usuario;

	public WelcomeLayout() {
		usuario = UserSession.get() == null ? null : UserSession.get().getUsuario();

		if (usuario instanceof Recepcionista) {
			addComponent(createRecepcionistaView());
		} else if (usuario instanceof Medico) {
			VerticalLayout l = createMedicoView();
			addComponent(l);
			setExpandRatio(l, 3L);
		}
	}

	private Label createRecepcionistaView() {
		return new Label("Bem vindo, " + usuario.getNome());
	}

	private VerticalLayout createMedicoView() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);

		layout.addComponent(new Label("Bem vindo, Dr. " + usuario.getNome()));
		layout.addComponent(new Label("Agenda de hoje:"));

		updateGrid();

		grid.setSizeFull();
		grid.removeAllColumns();
		grid.addColumn(//
				o -> {
					if (o.getData() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					return format.format(o.getData());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Horário");
		grid.addColumn(o -> o.getPaciente().getNome())//
				.setWidth(Constants.LARGE_FIELD)//
				.setCaption("Paciente");

		layout.addComponent(grid);

		return layout;
	}

	private void updateGrid() {
		Map<String, Object> params = new HashMap<>();
		params.put("medico", (Medico) usuario);
		params.put("data#ge", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		params.put("data#lt", DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.DAY_OF_MONTH));
		grid.setItems(service.list(params));
	}
}
