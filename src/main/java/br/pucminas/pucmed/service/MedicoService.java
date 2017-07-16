package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Usuario;

@Service
public class MedicoService extends BaseService<Medico> {
	public MedicoService() {
		super(Medico.class);
	}

	public Medico getByUsuario(Usuario usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("usuario", usuario);
		List<Medico> medicos = super.list(params);
		if (medicos.isEmpty())
			return null;
		return medicos.get(0);
	}
}
