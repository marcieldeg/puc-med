package br.pucminas.pucmed.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.postgresql.util.PSQLException;

public class Utils {
	private static final ZoneId zoneId = ZoneId.systemDefault();

	public static Date convertLocalDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(zoneId).toInstant());
	}

	public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(zoneId).toInstant());
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
