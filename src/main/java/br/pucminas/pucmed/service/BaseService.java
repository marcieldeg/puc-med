package br.pucminas.pucmed.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.pucmed.model.BaseModel;
import br.pucminas.pucmed.utils.CriteriaUtils;

// @Transactional(rollbackFor = Exception.class)
public abstract class BaseService<T extends BaseModel> {
	@Autowired
	private HibernateTemplate hibernateTemplate;

	private final Class<T> entityClass;

	protected BaseService(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public List<T> list() {
		return hibernateTemplate.loadAll(entityClass);
	}

	@SuppressWarnings("unchecked")
	public List<T> list(Map<String, Object> parameters) {
		return (List<T>) hibernateTemplate.findByCriteria(CriteriaUtils.createCriteria(entityClass, parameters));
	}

	@SuppressWarnings("unchecked")
	public List<T> list(T model) {
		return (List<T>) hibernateTemplate.findByCriteria(CriteriaUtils.createCriteria(model));
	}

	public T get(Long id) {
		return hibernateTemplate.get(entityClass, id);
	}

	//@Transactional(rollbackFor = Exception.class)
	public T insert(T entity) {
		hibernateTemplate.save(entity);
		return entity;
	}

	//@Transactional(rollbackFor = Exception.class)
	public T update(T entity) {
		hibernateTemplate.update(entity);
		return entity;
	}

	//@Transactional(rollbackFor = Exception.class)
	public void delete(T entity) {
		hibernateTemplate.delete(entity);
	}
}
