package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Medico;

@Service
public class MedicoService extends BaseService<Medico> {
	public MedicoService() {
		super(Medico.class);
	}
}
