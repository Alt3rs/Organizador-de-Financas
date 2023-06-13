package com.organizadororcamentopessoal.tempo;

import com.organizadororcamentopessoal.datasource.MovimentacaoDao;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

public class Tempo {
    public static final long SEC_IN_MILLI = 1_000;
    public static final long MIN_IN_MILLI = SEC_IN_MILLI * 60;
    public static final long HORA_IN_MILLI = MIN_IN_MILLI * 60;
    public static final long DIA_IN_MILLI = HORA_IN_MILLI * 24;
    public static final long SEMANA_IN_MILLI = DIA_IN_MILLI * 7;
    public static final long MES_IN_MILLI = DIA_IN_MILLI * 30;
    public static final long ANO_IN_MILLI = DIA_IN_MILLI * 365;

    public static long localDateStartInMilli(LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        return date.atStartOfDay(zone).toEpochSecond() * 1000;
    }
    public static long localDateEndInMilli(LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        return date.plusDays(1).atStartOfDay(zone).toEpochSecond() * 1000 -1;
    }

    public static long getTodayStartInMilli() {
        return localDateStartInMilli(LocalDate.now());
    }

    public static long getTodayEndInMilli() {
        return localDateEndInMilli(LocalDate.now());
    }

    public static Date getTodayStart() {
        return new Date(getTodayStartInMilli());
    }

    public static Date getTodayEnd() {
        return new Date(getTodayEndInMilli());
    }

    public static long epochMilliTo(long epoch, int unidade) {
        if(unidade == MovimentacaoDao.MINUTO) {
            return epoch / MIN_IN_MILLI;
        } else if(unidade == MovimentacaoDao.HORA) {
            return epoch / HORA_IN_MILLI;
        } else if(unidade == MovimentacaoDao.DIA) {
            return epoch / DIA_IN_MILLI;
        } else if(unidade == MovimentacaoDao.SEMANA) {
            return epoch / SEMANA_IN_MILLI;
        } else if(unidade == MovimentacaoDao.MES) {
            return epoch / MES_IN_MILLI;
        } else if(unidade == MovimentacaoDao.ANO){
            return epoch / ANO_IN_MILLI;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
