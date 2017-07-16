package br.pucminas.pucmed.reports;

import java.io.InputStream;
import java.sql.Connection;
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

	public byte[] runReceituario(Long idAtendimento) {
		try {
			Connection connection = sessionFactoryBean.getObject().getSessionFactoryOptions().getServiceRegistry()
					.getService(ConnectionProvider.class).getConnection();

			ClassLoader classLoader = getClass().getClassLoader();
			InputStream is = classLoader.getResource("receituario.jrxml").openStream();
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
