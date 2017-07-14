package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Atendimento;

@Service
public class AtendimentoService extends BaseService<Atendimento> {
	public AtendimentoService() {
		super(Atendimento.class);
	}
}
