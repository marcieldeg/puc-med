package br.pucminas.pucmed.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MedicoExpediente implements BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_medico")
	private Medico medico;

	@NotNull
	private DiaSemana diaSemana;

	@NotNull
	private Turno turno;

	public enum DiaSemana {
		DOMINGO, SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO;

		@Override
		public String toString() {
			switch (this) {
			case DOMINGO:
				return "Domingo";
			case SEGUNDA:
				return "Segunda";
			case TERCA:
				return "Terça";
			case QUARTA:
				return "Quarta";
			case QUINTA:
				return "Quinta";
			case SEXTA:
				return "Sexta";
			case SABADO:
				return "Sábado";
			default:
				return "";
			}
		}
	}

	public enum Turno {
		MANHA, TARDE, INTEGRAL;

		@Override
		public String toString() {
			switch (this) {
			case MANHA:
				return "Manhã";
			case TARDE:
				return "Tarde";
			case INTEGRAL:
				return "Integral";
			default:
				return "";
			}
		}
	}
}
