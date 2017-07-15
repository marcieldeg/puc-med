package br.pucminas.pucmed.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import br.pucminas.pucmed.model.BaseModel;

public class CriteriaUtils {
	private static final Log log = LogFactory.getLog(CriteriaUtils.class);

	public static DetachedCriteria createCriteria(BaseModel model) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(model.getClass());
		Field[] fields = model.getClass().getDeclaredFields();

		for (Field field : fields) {
			Object value = null;
			try {
				if (!field.isAccessible())
					field.setAccessible(true);
				value = field.get(model);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(e);
			}

			if (value != null) {
				Property property = Property.forName(field.getName());
				if (field.getType().equals(String.class))
					detachedCriteria.add(property.like(value));
				else
					detachedCriteria.add(property.eq(value));
			}
		}

		return detachedCriteria;
	}

	public static DetachedCriteria createCriteria(Class<?> baseClass, Map<String, Object> parameters) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(baseClass);

		for (Entry<String, Object> e : parameters.entrySet()) {
			String name = e.getKey();
			Object value = e.getValue();
			Operation operation = Operation.EQ;

			String[] splitted = name.split("#");
			String key = splitted[0];
			if (splitted.length > 1) {
				String op = splitted[1];
				if (!StringUtils.isEmpty(op))
					operation = Operation.valueOf(op.toUpperCase());
			}

			Property property = Property.forName(key);
			switch (operation) {
			case EQ:
				detachedCriteria.add(property.eq(value));
				break;
			case NE:
				detachedCriteria.add(property.ne(value));
				break;
			case GE:
				detachedCriteria.add(property.ge(value));
				break;
			case GT:
				detachedCriteria.add(property.gt(value));
				break;
			case IN:
				detachedCriteria.add(property.in(getLongArrayFromString(value.toString())));
				break;
			case ISNOTNULL:
				detachedCriteria.add(property.isNotNull());
				break;
			case ISNULL:
				detachedCriteria.add(property.isNull());
				break;
			case LE:
				detachedCriteria.add(property.le(value));
				break;
			case LIKE:
				detachedCriteria.add(property.like(value));
				break;
			case LT:
				detachedCriteria.add(property.lt(value));
				break;
			case EXISTS:
				detachedCriteria.createCriteria(key).add(Restrictions.eq("id", value));
			default:
				break;
			}
		}

		return detachedCriteria;
	}

	private static Object[] getLongArrayFromString(String input) {
		String[] items = input.toString().split(",");
		Long[] result = new Long[items.length];
		for (int i = 0; i < items.length; i++)
			result[i] = Long.parseLong(items[i]);
		return result;
	}

	private enum Operation {
		IN, LIKE, EQ, NE, LT, GT, LE, GE, ISNULL, ISNOTNULL, EXISTS
	}
}
