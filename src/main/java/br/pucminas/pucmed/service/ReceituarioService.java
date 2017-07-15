package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Receituario;

@Service
public class ReceituarioService extends BaseService<Receituario> {
	public ReceituarioService() {
		super(Receituario.class);
	}
}
