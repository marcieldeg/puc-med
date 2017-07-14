package br.pucminas.pucmed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.pucminas.pucmed.enums.Status;
import br.pucminas.pucmed.model.Atendente;
import br.pucminas.pucmed.service.AtendenteService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MedicoTest {

	@Test
	public void contextLoads() {
	}

	@Autowired
	AtendenteService s;

	@Test
	public void test() {
		Atendente a = new Atendente();
		a.setNome("Marciel");
		a.setLogin("login");
		a.setSenha("senha");
		a.setEmail("e@mail.com");
		a.setStatus(Status.ATIVO);
		s.insert(a);
	}

	/*
	 * @Autowired MedicoDAO medicoDAO;
	 * 
	 * @Test
	 * 
	 * @Rollback(value = true) public void insertMedico() { Medico m =
	 * Medico.builder().nome("ASTROGILDO").crm(5555L).build();
	 * 
	 * medicoDAO.insert(m);
	 * 
	 * Assert.assertNotNull("Expect not null object", m);
	 * Assert.assertNotNull("Expect not null ID", m.getId()); }
	 * 
	 * @Test
	 * 
	 * @Rollback(value = true) public void getMedico() { Medico m =
	 * medicoDAO.get(1L);
	 * 
	 * System.err.println(m);
	 * 
	 * Assert.assertNotNull("Expect not null object", m); }
	 */
}
