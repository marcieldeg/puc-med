package br.pucminas.pucmed.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.postgresql.util.PSQLException;

public class Utils {
	public static Date convertLocalDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().toInstant(Constants.ZONE_OFFSET));
	}

	public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.toInstant(Constants.ZONE_OFFSET));
	}

	public static String translateExceptionMessage(PSQLException exception) {
		switch (exception.getSQLState()) {
		case "23503":
			return "Não é possível excluir esse registro pois existem registros relacionados.";
		// TODO: outras mensagens de erro
		default:
			return "Erro " + exception.getSQLState() + " - " + exception.getServerErrorMessage();
		}
	}
}
