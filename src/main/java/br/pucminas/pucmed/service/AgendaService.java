package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Agenda;

@Service
public class AgendaService extends BaseService<Agenda> {
	public AgendaService() {
		super(Agenda.class);
	}
	
	public void validate(Agenda entity) throws DataIntegrityViolationException {
		Map<String, Object> params = new HashMap<>();
		params.put("medico", entity.getMedico());
		params.put("data", entity.getData());
		List<Agenda> list = this.list(params);
		if (!list.isEmpty())
			throw new DataIntegrityViolationException("Horário já ocupado por outro agendamento");
	}

	@Override
	public void preInsert(Agenda entity) throws DataIntegrityViolationException {
		validate(entity);
	}

	@Override
	public void preUpdate(Agenda entity) throws DataIntegrityViolationException {
		validate(entity);
	}
}
