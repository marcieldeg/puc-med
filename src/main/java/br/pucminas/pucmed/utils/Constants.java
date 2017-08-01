package br.pucminas.pucmed.utils;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final String APPLICATION_TITLE = "PUC-MED";

	public static final int XSMALL_FIELD = 40;
	public static final int SMALL_FIELD = XSMALL_FIELD * 2;
	public static final int MEDIUM_FIELD = SMALL_FIELD * 2;
	public static final int LARGE_FIELD = MEDIUM_FIELD * 2;
	public static final int XLARGE_FIELD = LARGE_FIELD * 2;

	public static final String SMALL_FIELD_STYLE = "m-small-field";
	public static final String MEDIUM_FIELD_STYLE = "m-medium-field";
	public static final String LARGE_FIELD_STYLE = "m-large-field";
	public static final String XLARGE_FIELD_STYLE = "m-xlarge-field";

	public static final List<Integer> TURNO_MANHA = Arrays.asList(8, 9, 10, 11);
	public static final List<Integer> TURNO_TARDE = Arrays.asList(14, 15, 16, 17);

	public static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00");
}
