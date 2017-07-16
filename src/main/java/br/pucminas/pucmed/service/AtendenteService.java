package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Atendente;
import br.pucminas.pucmed.model.Usuario;

@Service
public class AtendenteService extends BaseService<Atendente> {
	public AtendenteService() {
		super(Atendente.class);
	}

	public Atendente getByUsuario(Usuario usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("usuario", usuario);
		List<Atendente> atendentes = super.list(params);
		if (atendentes.isEmpty())
			return null;
		return atendentes.get(0);
	}
}
