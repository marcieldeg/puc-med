package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.TipoExame;

@Service
public class TipoExameService extends BaseService<TipoExame> {
	public TipoExameService() {
		super(TipoExame.class);
	}
}
