package br.pucminas.pucmed.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Paciente implements BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String nome;

	@NotNull
	private Sexo sexo;

	@NotNull
	private Date dataNascimento;

	@NotNull
	@Size(max = 11)
	@Pattern(regexp = "^[0-9]{11}")
	private String cpf;

	/* ENDEREÇO */
	@NotNull
	private String endereco;

	private Long numero;

	private String complemento;

	private String bairro;
	
	private String cidade;

	@ManyToOne
	@JoinColumn(name = "id_estado")
	private Estado estado;

	@Size(max = 8)
	private String cep;

	@Email
	private String email;

	private String telefone;

	public enum Sexo {
		MASCULINO("M"), FEMININO("F");

		private String value;

		private Sexo(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
