package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Laboratorio;
import br.pucminas.pucmed.model.Usuario;

@Service
public class LaboratorioService extends BaseService<Laboratorio> {
	public LaboratorioService() {
		super(Laboratorio.class);
	}

	public Laboratorio getByUsuario(Usuario usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("usuario", usuario);
		List<Laboratorio> l = super.list(params);
		if (l.isEmpty())
			return null;
		return l.get(0);
	}
}
