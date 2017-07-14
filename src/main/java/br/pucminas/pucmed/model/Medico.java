package br.pucminas.pucmed.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name="id")
public class Medico extends Usuario {
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
}
