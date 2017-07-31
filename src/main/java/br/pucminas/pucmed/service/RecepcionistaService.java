package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Recepcionista;
import br.pucminas.pucmed.model.Usuario;

@Service
public class RecepcionistaService extends UsuarioBaseService<Recepcionista> {
	public RecepcionistaService() {
		super(Recepcionista.class);
	}

	public Recepcionista getByUsuario(Usuario usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("usuario", usuario);
		List<Recepcionista> l = super.list(params);
		if (l.isEmpty())
			return null;
		return l.get(0);
	}
}
