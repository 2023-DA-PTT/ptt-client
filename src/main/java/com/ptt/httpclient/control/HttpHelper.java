package com.ptt.httpclient.control;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class HttpHelper {
    public static String parseRequestBody(String bodyTemplate, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();

        char prev = '_';
        boolean inVarName = false;
        StringBuilder varName = new StringBuilder();

        for (char c : bodyTemplate.toCharArray()) {
            if (inVarName && prev == '}' && c == '}') {
                inVarName = false;
                String param = params.get(StringUtils.deleteWhitespace(varName.toString()));
                if (param == null) {
                    throw new IllegalArgumentException("the param map didn't include needed parameter");
                }
                stringBuilder.append(param);
                varName.setLength(0); // clears the builder
            }else if(inVarName) {
                if (prev == '}') {
                    varName.append(prev);
                }
                if(c != '}') {
                    varName.append(c);
                }
            }else if (prev == '{' && c == '{') {
                inVarName = true;
            }else {
                if (prev == '{') {
                    stringBuilder.append(prev);
                }
                if(c != '{') {
                    stringBuilder.append(c);
                }
            }
            prev = c;
        }

        return stringBuilder.toString();
    }

    public static String parseRequestUrl(String urlTemplate, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean inVarName = false;
        StringBuilder varName = new StringBuilder();
        for(char c : urlTemplate.toCharArray()) {
            if(inVarName && c == '}') {
                inVarName = false;
                String param = params.get(StringUtils.deleteWhitespace(varName.toString()));
                if (param == null) {
                    throw new IllegalArgumentException("the param map didn't include needed parameter");
                }
                stringBuilder.append(param);
                varName.setLength(0); // clears the builder
            } else if(inVarName) {
                varName.append(c);
            } else if(c == '{') {
                inVarName = true;
            }else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
}
