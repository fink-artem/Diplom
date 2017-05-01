package com.fink.logic;

public enum SemanRel {

    AUTHOR, AGENT, ADR, IN_DIRECT,  IN_ACCORD, TIME, TYPE_OF, VALUE, IDENT, NAME, INSTR, SRC_PNT, C_AGENT,
    QUANTIT, TRG_PNT, LOK, SCALE, MATER, PURP, OBJ, RESTR, ESTIM, PARAM, PACIEN, WITH,
    MEDIATOR, PROPERT, BELNG, CAUSE, RESLT, CONTEN, METHOD, MEANS, DEGREE, SUB, THEME, AIM, PART, F_ACT, S_ACT, ACT, UNDEFINED;

    public static SemanRel convert(String text) {
        switch (text) {
            case "AUTHOR":
                return AUTHOR;
            case "AGENT":
                return AGENT;
            case "ADR":
                return ADR;
            case "IN-DIRECT":
                return IN_DIRECT;
            case "IN-ACCORD":
                return  IN_ACCORD;
            case "TIME":
                return TIME;
            case "TYPE-OF":
                return TYPE_OF;
            case "VALUE":
                return VALUE;
            case "IDENT":
                return IDENT;
            case "NAME":
                return NAME;
            case "INSTR":
                return INSTR;
            case "SRC-PNT":
                return SRC_PNT;
            case "C-AGENT":
                return C_AGENT;
            case "QUANTIT":
                return QUANTIT;
            case "TRG-PNT":
                return TRG_PNT;
            case "LOK":
                return LOK;
            case "SCALE":
                return SCALE;
            case "MATER":
                return MATER;
            case "PURP":
                return PURP;
            case "OBJ":
                return OBJ;
            case "RESTR":
                return RESTR;
            case "ESTIM":
                return ESTIM;
            case "PARAM":
                return PARAM;
            case "PACIEN":
                return PACIEN;
            case "WITH":
                return WITH;
            case "MEDIATOR":
                return MEDIATOR;
            case "PROPERT":
                return PROPERT;
            case "BELNG":
                return BELNG;
            case "CAUSE":
                return CAUSE;
            case "RESLT":
                return RESLT;
            case "CONTEN":
                return CONTEN;
            case "METHOD":
                return METHOD;
            case "MEANS":
                return MEANS;
            case "DEGREE":
                return DEGREE;
            case "SUB":
                return SUB;
            case "THEME":
                return THEME;
            case "AIM":
                return AIM;
            case "PART":
                return PART;
            case "F-ACT":
                return F_ACT;
            case "S-ACT":
                return S_ACT;
            case "ACT":
                return ACT;
            default:
                return UNDEFINED;
        }
    }

}
