package br.pucminas.pucmed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PacienteTest {
	@Test
	public void contextLoads() {
	}

	// @Autowired
	// PacienteService service;
	//
	// @Test
	// public void crudPaciente() {
	// Paciente p = service.get(1L);
	// Assert.assertNull("Expect null object", p);
	//
	// /*INSERT*/
	// p = Paciente.builder() //
	// .nome("Marciel") //
	// .dataNascimento(new Date()) //
	// .cpf("09801974737") //
	// .sexo(Sexo.MASCULINO) //
	// .endereco("Rua Rio Grande do Sul") //
	// .cep("29175117") //
	// .build();
	// p = service.insert(p);
	//
	// Assert.assertNotNull("Expect not null object", p);
	// Assert.assertNotNull("Expect not null ID", p.getId());
	//
	// Long id = p.getId();
	//
	// p = service.get(id);
	// Assert.assertNotNull("Expect not null object", p);
	//
	// /*UPDATE*/
	// p.setBairro("Estância Monazítica");
	// p = service.insert(p);
	// Assert.assertNotNull("Expect not null object", p);
	//
	// p = service.get(id);
	// Assert.assertNotNull("Expect not null \"bairro\"", p.getBairro());
	//
	// /*DELETE*/
	// service.delete(p);
	//
	// p = service.get(id);
	// Assert.assertNull("Expect null object", p);
	// }
	//
	// @Test
	// public void insertPacienteComEndereco() {
	// Paciente p = Paciente.builder() //
	// .nome("Marciel") //
	// .dataNascimento(new Date()) //
	// .cpf("09801974737") //
	// .sexo(Sexo.MASCULINO) //
	// .build();
	//
	// Cidade c = Cidade.builder().name("SANTA
	// TERESA").id(123L).estado(Estado.builder().name("ESPIRITO
	// SANTO").sigla("ES").id(123L).build()).build();
	// p.setCidade(c);
	// p.setEndereco("SÃO SEBASTIÃO");
	// p.setCep("29650000");
	//
	// p = service.insert(p);
	//
	// Assert.assertNotNull("Expect not null object", p);
	// Assert.assertNotNull("Expect not null ID", p.getId());
	// }
	//
	// @Test
	// public void findList() {
	// Map<String, Object> p = new HashMap<>();
	// p.put("nome#like", "Marcie%");
	//
	// List<Paciente> l = service.list(p);
	//
	// Assert.assertNotNull("Expect not null object", l);
	// Assert.assertNotEquals("Expect not empty list", l.size(), 0);
	// }
	//
	// @Test
	// public void listByExample() {
	// Paciente p = Paciente.builder() //
	// .nome("Marcie%") //
	// .build();
	//
	// List<Paciente> l = service.list(p);
	//
	// Assert.assertNotNull("Expect not null object", l);
	// Assert.assertNotEquals("Expect not empty list", l.size(), 0);
	// }
}
