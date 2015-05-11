package org.motechproject.ebodac.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents language of Subject
 */
public enum Language {
    English("en"),
    Krio("kri"),
    Limba("lma"),
    Susu("sus"),
    Temne("tem");

    private String code;

    public static Language getByCode(String code) {
        for(Language e: Language.values()) {
            if(e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static List<String> getListOfCodes() {
        List<String> codes = new ArrayList<>();

        for (Language language : values()) {
            codes.add(language.getCode());
        }
        return codes;
    }

    private Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}
