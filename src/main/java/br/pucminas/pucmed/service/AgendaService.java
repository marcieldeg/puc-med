package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Agenda;

@Service
public class AgendaService extends BaseService<Agenda> {
	public AgendaService() {
		super(Agenda.class);
	}
}
