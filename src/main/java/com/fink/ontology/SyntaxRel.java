package com.fink.ontology;

public enum SyntaxRel {

    KOLICH, CYCH_CHISL, FIO, NAR_PRIL, ODNOR_PRIL, ODNOR_NAR, ODNOR_INF, DATE,
    CLOSCH_PG, SRAVN_STEPEN, NARECH_GLAGOL, PRIL_CYSCH, NAR_CHISL_CYSCH,
    CHISL_CYSCH, GENIT_IG, PG, ODNOR_IG, OTR_FORMA, PRYAM_DOP, EL_ADRES, PER_GLAG_INF, PODL, UNDEFINED;

    public static SyntaxRel convert(String text) {
        switch (text) {
            case "КОЛИЧ":
                return KOLICH;
            case "СУЩ-ЧИСЛ":
                return CYCH_CHISL;
            case "ФИО":
                return FIO;
            case "НАР_ПРИЛ":
                return NAR_PRIL;
            case "ОДНОР_ПРИЛ":
                return ODNOR_PRIL;
            case "ОДНОР_НАР":
                return ODNOR_NAR;
            case "ОДНОР_ИНФ":
                return ODNOR_INF;
            case "ДАТА":
                return DATE;
            case "СЛОЖ_ПГ":
                return CLOSCH_PG;
            case "СРАВН-СТЕПЕНЬ":
                return SRAVN_STEPEN;
            case "НАРЕЧ-ГЛАГОЛ":
                return NARECH_GLAGOL;
            case "ПРИЛ_СУЩ":
                return PRIL_CYSCH;
            case "НАР-ЧИСЛ-СУЩ":
                return NAR_CHISL_CYSCH;
            case "ЧИСЛ-СУЩ":
                return CHISL_CYSCH;
            case "ГЕНИТ_ИГ":
                return GENIT_IG;
            case "ПГ":
                return PG;
            case "ОДНОР_ИГ":
                return ODNOR_IG;
            case "ОТР_ФОРМА":
                return OTR_FORMA;
            case "ПРЯМ_ДОП":
                return PRYAM_DOP;
            case "ЭЛ_АДРЕС":
                return EL_ADRES;
            case "ПЕР_ГЛАГ_ИНФ":
                return PER_GLAG_INF;
            case "ПОДЛ":
                return PODL;
            default:
                return UNDEFINED;
        }
    }
}
