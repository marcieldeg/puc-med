package br.pucminas.pucmed.reports;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import br.pucminas.pucmed.bean.BeanGetter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class ReportsRunner {
	private LocalSessionFactoryBean sessionFactoryBean = BeanGetter.getBean(LocalSessionFactoryBean.class);

	private static final String RECEITUARIO_NAME = "receituario.jrxml";
	private static final String EXAME_NAME = "exame.jrxml";

	private Connection getConnection() throws SQLException {
		return sessionFactoryBean.getObject().getSessionFactoryOptions().getServiceRegistry()
				.getService(ConnectionProvider.class).getConnection();
	}

	private byte[] runReport(String name, Long id) {
		try {
			Connection connection = getConnection();

			ClassLoader classLoader = getClass().getClassLoader();
			InputStream is = classLoader.getResource(name).openStream();
			JasperReport jasperReport = JasperCompileManager.compileReport(is);
			HashMap<String, Object> params = new HashMap<>();
			params.put("id", id);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] runReceituario(Long idAtendimento) {
		return runReport(RECEITUARIO_NAME, idAtendimento);
	}

	public byte[] runExame(Long idExame) {
		return runReport(EXAME_NAME, idExame);
	}
}
