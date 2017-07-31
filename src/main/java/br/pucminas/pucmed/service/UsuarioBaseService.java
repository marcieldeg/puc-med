package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import br.pucminas.pucmed.model.Usuario;

public abstract class UsuarioBaseService<T extends Usuario> extends BaseService<T> {
	@Autowired
	UsuarioService usuarioService;

	protected UsuarioBaseService(Class<T> entityClass) {
		super(entityClass);
	}
	
	public void validate(T entity) throws DataIntegrityViolationException {
		Map<String, Object> params = new HashMap<>();
		params.put("login", entity.getLogin());
		List<Usuario> list = usuarioService.list(params);
		if (!list.isEmpty())
			throw new DataIntegrityViolationException("Login já cadastrado para outro usuário");

		params = new HashMap<>();
		params.put("email", entity.getEmail());
		list = usuarioService.list(params);
		if (!list.isEmpty())
			throw new DataIntegrityViolationException("E-mail já cadastrado para outro usuário");
	}

	@Override
	public void preInsert(T entity) throws DataIntegrityViolationException {
		validate(entity);
	}

	@Override
	public void preUpdate(T entity) throws DataIntegrityViolationException {
		validate(entity);
	}
}
