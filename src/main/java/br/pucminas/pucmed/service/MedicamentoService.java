package br.pucminas.pucmed.service;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Medicamento;

@Service
public class MedicamentoService extends BaseService<Medicamento> {
	public MedicamentoService() {
		super(Medicamento.class);
	}
}
