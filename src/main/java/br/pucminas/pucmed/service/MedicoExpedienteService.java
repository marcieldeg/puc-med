package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.MedicoExpediente;

@Service
public class MedicoExpedienteService extends BaseService<MedicoExpediente> {
	public MedicoExpedienteService() {
		super(MedicoExpediente.class);
	}
}
