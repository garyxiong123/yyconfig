/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.*;

/**
 * @author dongdong
 * @date 2018/10/16
 */
public class FileUploadHelper {

    public static void uploadFile(InputStream inputStream, String fileName, String path) throws IOException {
        path = formatPath(path);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        try (
                FileOutputStream outputStream = new FileOutputStream(new File(file, fileName))
        ) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    public static void downloadFile(String fileName, String path, OutputStream outputStream) throws IOException {
        path = formatPath(path);
        try (
                FileInputStream inputStream = new FileInputStream(path + fileName);
        ) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private static String formatPath(String path) {
        String fileSeparator = "/";
        if (!path.endsWith(fileSeparator)) {
            path += fileSeparator;
        }
        return path;
    }


    public static String addTimeToFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        fileName = fileName.trim();
        int position = fileName.lastIndexOf(".");
        String suffix = "";
        if (position != -1) {
            suffix = fileName.substring(position);
            fileName = fileName.substring(0, position);
        }
        return fileName + "_" + System.currentTimeMillis() + suffix;
    }


}
