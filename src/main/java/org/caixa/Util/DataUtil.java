package org.caixa.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

    private final static String FORMATO = "dd/MM/yyyy";

    public static Date getDataFormatada(String data) throws ParseException {
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula");
        }
        else if (data.length() != 10 || !data.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new IllegalArgumentException("Data deve estar no formato dd/MM/yyyy");
        } else if ( 0 >= Integer.parseInt(data.split("/")[0]) || Integer.parseInt(data.split("/")[0]) >= 32) {
            throw new IllegalArgumentException("O dia deve estar entre 1 e 31");
        }else if ( 0 >= Integer.parseInt(data.split("/")[1]) || Integer.parseInt(data.split("/")[1]) >= 13) {
            throw new IllegalArgumentException("O mes deve estar entre 1 e 12");
        }
        SimpleDateFormat formatter = new SimpleDateFormat(FORMATO);
        return formatter.parse(data);
    }

    public static String getDataFormatada(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMATO);
        return formatter.format(data);
    }
}
