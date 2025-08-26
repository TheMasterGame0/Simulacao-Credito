package org.caixa.Util;

import org.caixa.Exception.ErroPrevistoException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

    private final static String FORMATO = "dd/MM/yyyy";
    private final static String YYYY_MM_DD_HIFEN = "yyyy-MM-dd";

    public static Date getDataFormatada(String data) {
        if (data == null) {
            throw new ErroPrevistoException("Data não pode ser nula");
        }
        else if (data.length() != 10 || !data.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new ErroPrevistoException("Data deve estar no formato dd/MM/yyyy");
        } else if ( 0 >= Integer.parseInt(data.split("/")[0]) || Integer.parseInt(data.split("/")[0]) >= 32) {
            throw new ErroPrevistoException("O dia deve estar entre 1 e 31");
        }else if ( 0 >= Integer.parseInt(data.split("/")[1]) || Integer.parseInt(data.split("/")[1]) >= 13) {
            throw new ErroPrevistoException("O mes deve estar entre 1 e 12");
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMATO);
            return formatter.parse(data);
        }catch (ParseException e){
            throw new ErroPrevistoException("Erro ao realizar parse da data: " + e.getMessage(), 500);
        }
    }

    public static String getDataFormatada(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_HIFEN);
        return formatter.format(data);
    }
}
