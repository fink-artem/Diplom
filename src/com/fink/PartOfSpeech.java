package com.fink;

public enum PartOfSpeech {

    ADJECTIVE, NOUN, VERB, PRETEXT, UNION, PUNCT, PARTICIPLE,COMMUNION, UNDEFINED;

    PartOfSpeech convert(String text) {
        switch (text) {
            case "ПРИЛАГАТЕЛЬНОЕ":
                return ADJECTIVE;
            case "СУЩЕСТВИТЕЛЬНОЕ":
                return NOUN;
            case "ПУНКТУАТОР":
                return PUNCT;
            case "ГЛАГОЛ":
                return VERB;
            case "ПРЕДЛОГ":
                return PRETEXT;
            case "СОЮЗ":
                return UNION;
             case "ПРИЧАСТИЕ":
                return COMMUNION;   
            case "ДЕЕПРИЧАСТИЕ":
                return PARTICIPLE;
            default:
                return UNDEFINED;
        }
    }
}
