package br.pucminas.pucmed.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Email;

import br.pucminas.pucmed.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Usuario implements BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 20)
	@Column(unique = true)
	private String nome;

	@NotNull
	@Size(min = 5, max = 20)
	private String senha;

	@NotNull
	@Email
	@Column(unique = true)
	private String email;

	@NotNull
	@ColumnDefault("1")
	private Status status;

	@NotNull
	private TipoUsuario tipoUsuario;

	public enum TipoUsuario {
		ADMINISTRADOR, ATENDENTE, MEDICO
	}
}
