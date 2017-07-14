package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Paciente;

@Service
public class PacienteService extends BaseService<Paciente> {
	public PacienteService() {
		super(Paciente.class);
	}
}
