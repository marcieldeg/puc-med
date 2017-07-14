package br.pucminas.pucmed.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.pucminas.pucmed.service.BaseService;

public class BeanGetter implements ApplicationContextAware {
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static <T extends BaseService<?>> T getService(Class<T> clazz) {
		return context.getBean(clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T extends BaseService<?>> T getService(String beanName) {
		return (T) context.getBean(beanName);
	}
}
