package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Paciente;

@Service
public class PacienteService extends BaseService<Paciente> {
	public PacienteService() {
		super(Paciente.class);
	}
	
	public void validate(Paciente entity) throws DataIntegrityViolationException {
		Map<String, Object> params = new HashMap<>();
		params.put("cpf", entity.getCpf());
		List<Paciente> list = this.list(params);
		if (!list.isEmpty())
			throw new DataIntegrityViolationException("CPF já cadastrado para outro paciente");
	}

	@Override
	public void preInsert(Paciente entity) throws DataIntegrityViolationException {
		validate(entity);
	}

	@Override
	public void preUpdate(Paciente entity) throws DataIntegrityViolationException {
		validate(entity);
	}
}
