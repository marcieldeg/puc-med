package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Exame;

@Service
public class ExameService extends BaseService<Exame> {
	public ExameService() {
		super(Exame.class);
	}
}
