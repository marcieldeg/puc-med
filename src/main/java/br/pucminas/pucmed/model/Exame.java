package br.pucminas.pucmed.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Exame implements BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_tipoExame")
	private TipoExame tipoExame;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_atendimento")
	private Atendimento atendimento;

	@Type(type = "date")
	private Date dataRealizacao;

	@Size(max = 4000)
	private String resultado;
}
