package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Especialidade;

@Service
public class EspecialidadeService extends BaseService<Especialidade> {
	public EspecialidadeService() {
		super(Especialidade.class);
	}
}
