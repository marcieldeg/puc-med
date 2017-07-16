package br.pucminas.pucmed.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Utils {
	public static Date convertLocalDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().toInstant(Constants.ZONE_OFFSET));
	}

	public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.toInstant(Constants.ZONE_OFFSET));
	}
}
