/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
