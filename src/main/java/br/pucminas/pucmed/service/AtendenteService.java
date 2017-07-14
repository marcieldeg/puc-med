package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Atendente;

@Service
public class AtendenteService extends BaseService<Atendente> {
	public AtendenteService() {
		super(Atendente.class);
	}
}
