package br.pucminas.pucmed.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Receituario implements BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_atendimento")
	private Atendimento atendimento;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_medicamento")
	private Medicamento medicamento;
	
	@NotNull
	private Long quantidade;

	@NotNull
	@Size(max = 4000)
	private String posologia;
}
