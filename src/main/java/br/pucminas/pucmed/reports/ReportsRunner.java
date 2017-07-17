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

	private Connection getConnection() throws SQLException {
		return sessionFactoryBean.getObject().getSessionFactoryOptions().getServiceRegistry()
				.getService(ConnectionProvider.class).getConnection();
	}

	public byte[] runReceituario(Long idAtendimento) {
		try {
			Connection connection = getConnection();

			ClassLoader classLoader = getClass().getClassLoader();
			InputStream is = classLoader.getResource(RECEITUARIO_NAME).openStream();
			JasperReport jasperReport = JasperCompileManager.compileReport(is);
			HashMap<String, Object> params = new HashMap<>();
			params.put("id", idAtendimento);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
