package com.yofish.apollo.enums;


import org.apache.commons.lang3.StringUtils;

public final class EnvsUtils {

    public static Envs transformEnv(String envName) {
        if (StringUtils.isBlank(envName)) {
            return Envs.UNKNOWN;
        }
        switch (envName.trim().toUpperCase()) {
            case "LPT":
                return Envs.LPT;
            case "FAT":
            case "FWS":
                return Envs.FAT;
            case "UAT":
                return Envs.UAT;
            case "PRO":
            case "PROD":
                return Envs.PROD;
            case "DEV":
                return Envs.DEV;
            case "LOCAL":
                return Envs.LOCAL;
            case "TEST":
                return Envs.TEST;
            case "PRE":
                return Envs.PRE;
            case "TOOLS":
                return Envs.TOOLS;
            default:
                return Envs.UNKNOWN;
        }
    }
}
