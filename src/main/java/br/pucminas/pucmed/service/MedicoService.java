package br.pucminas.pucmed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.pucminas.pucmed.model.Medico;
import br.pucminas.pucmed.model.Usuario;

@Service
public class MedicoService extends UsuarioBaseService<Medico> {
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
	
	public void validate(Medico entity) throws DataIntegrityViolationException {
		Map<String, Object> params = new HashMap<>();
		params.put("crm", entity.getCrm());
		List<Medico> list = this.list(params);
		if (!list.isEmpty())
			throw new DataIntegrityViolationException("CRM já cadastrado para outro usuário");
		
		super.validate(entity);
	}
}
