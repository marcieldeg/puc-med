package br.pucminas.pucmed.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Medico implements BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String nome;

	@NotNull
	private Long crm;

	@NotEmpty
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "medicoespecialidade", //
			joinColumns = @JoinColumn(name = "id_medico"), //
			inverseJoinColumns = @JoinColumn(name = "id_especialidade") //
	)
	private Set<Especialidade> especialidades;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "medico")
	private Set<MedicoExpediente> expediente;

	@NotNull
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
}
