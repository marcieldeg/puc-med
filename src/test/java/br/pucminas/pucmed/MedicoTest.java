package br.pucminas.pucmed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MedicoTest {

	@Test
	public void contextLoads() {
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
