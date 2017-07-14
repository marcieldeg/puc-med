package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Usuario;

@Service
public class UsuarioService extends BaseService<Usuario> {
	public UsuarioService() {
		super(Usuario.class);
	}

	public Usuario getByLogin(String nome, String senha) {
		Map<String, Object> params = new HashMap<>();
		params.put("nome", nome);
		params.put("senha", senha);
		List<Usuario> usuarios = super.list(params);
		if (usuarios.size() == 0)
			return null;
		return usuarios.get(0);
	}

	public Usuario getByEmail(String email) {
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		List<Usuario> usuarios = super.list(params);
		if (usuarios.size() == 0)
			return null;
		return usuarios.get(0);
	}
}
