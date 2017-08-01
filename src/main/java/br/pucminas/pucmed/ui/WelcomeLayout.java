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
import br.pucminas.pucmed.model.Laboratorio;
import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Recepcionista;
import br.pucminas.pucmed.model.Usuario;
import br.pucminas.pucmed.service.AgendaService;
import br.pucminas.pucmed.service.ExameService;
import br.pucminas.pucmed.utils.Constants;

@SuppressWarnings("serial")
public class WelcomeLayout extends VerticalLayout {
	private final Grid<Agenda> gridMedico = new Grid<>();
	
	private final AgendaService agendaService = BeanGetter.getService(AgendaService.class);
	private final ExameService exameService = BeanGetter.getService(ExameService.class);

	private Usuario usuario;

	public WelcomeLayout() {
		setSizeFull();
		usuario = UserSession.get() == null ? null : UserSession.get().getUsuario();

		if (usuario instanceof Recepcionista) {
			createRecepcionistaView();
		} else if (usuario instanceof Medico) {
			createMedicoView();
		} else if (usuario instanceof Laboratorio) {
			createLaboratorioView();
		}
	}

	private void createRecepcionistaView() {
		addComponent(new Label("Bem vindo, " + usuario.getNome() + "."));
	}

	private void createLaboratorioView() {
		Map<String, Object> params = new HashMap<>();
		params.put("dataRealizacao#isnull", null);
		int examesCount = exameService.list(params).size();
		
		String message = "";
		if (examesCount == 0)
			message = "Não existem exames a serem realizados.";
		else if (examesCount == 1)
			message = "Há 1 exame a ser realizado.";
		else
			message = String.format("Há %d exames a serem realizados.", examesCount);
		
		addComponent(new Label("Bem vindo, " + usuario.getNome() + "."));
		Label examesLabel = new Label(message);
		addComponent(examesLabel);
		setExpandRatio(examesLabel, 1);
	}

	private void createMedicoView() {
		addComponent(new Label("Bem vindo, Dr. " + usuario.getNome() + "."));
		addComponent(new Label("Agenda de hoje:"));

		updateGridMedicos();

		gridMedico.setSizeFull();
		gridMedico.removeAllColumns();
		gridMedico.addColumn(//
				o -> {
					if (o.getData() == null)
						return null;
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					return format.format(o.getData());
				})//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Horário");
		gridMedico.addColumn(o -> o.getPaciente().getNome())//
				.setWidth(Constants.MEDIUM_FIELD)//
				.setCaption("Paciente");

		addComponent(gridMedico);
		setExpandRatio(gridMedico, 1);
	}

	private void updateGridMedicos() {
		Map<String, Object> params = new HashMap<>();
		params.put("medico", (Medico) usuario);
		params.put("data#ge", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		params.put("data#lt", DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.DAY_OF_MONTH));
		gridMedico.setItems(agendaService.list(params));
	}
}
