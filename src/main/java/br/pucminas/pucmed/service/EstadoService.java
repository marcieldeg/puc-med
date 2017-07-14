package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Estado;

@Service
public class EstadoService extends BaseService<Estado> {
	public EstadoService() {
		super(Estado.class);
	}
}
