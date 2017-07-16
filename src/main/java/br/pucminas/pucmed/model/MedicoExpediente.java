package br.pucminas.pucmed.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import br.pucminas.pucmed.utils.Constants;
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

		public static DiaSemana fromDate(Date date) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int dia = c.get(Calendar.DAY_OF_WEEK);
			return DiaSemana.values()[dia - 1];
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

		public static Turno fromDate(Date date) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int hora = c.get(Calendar.HOUR_OF_DAY);
			if (Constants.TURNO_MANHA.contains(hora))
				return Turno.MANHA;
			if (Constants.TURNO_TARDE.contains(hora))
				return Turno.TARDE;
			throw new IndexOutOfBoundsException("Fora do período de trabalho");
		}
	}
}
