package com.ptt.boundary.httpclient;

import org.junit.jupiter.api.Test;

import com.ptt.entities.OutputType;
import com.ptt.entities.ParameterValue;
import com.ptt.httpclient.control.HttpHelper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpHelperTest {

    @Test
    void parseRequestBodyWorking() {
        String result = HttpHelper.parseRequestBody(
                """
                        {
                            name: '{{param1}}'
                        }
                        """, Map.of("param1", new ParameterValue("result2", OutputType.PLAIN_TEXT))
        );
        assertEquals(
                """
                        {
                            name: 'result2'
                        }
                        """,
                result
        );
    }

    @Test
    void parseRequestBodyWithWhitespaces() {
        String result = HttpHelper.parseRequestBody(
                """
                        {
                            name: '{{  param1       }}'
                        }
                        """, Map.of("param1", new ParameterValue("result2", OutputType.PLAIN_TEXT))
        );
        assertEquals(
                """
                        {
                            name: 'result2'
                        }
                        """,
                result
        );
    }

    @Test
    void parseRequestBodyWithDeepObjects() {
        String result = HttpHelper.parseRequestBody(
                """
                        {
                            person: {
                                name: "{{personName}}"
                                address: {
                                    street: '{{ streetName }}',
                                    streetNumber: {{streetNumber}}
                                },
                                {{customObj}}
                            }
                        }
                        """, Map.of(
                                "personName", new ParameterValue("Herbert", OutputType.PLAIN_TEXT),
                                "streetName", new ParameterValue("HerbertStreet", OutputType.PLAIN_TEXT),
                                "streetNumber", new ParameterValue("15", OutputType.PLAIN_TEXT),
                                "customObj", new ParameterValue("""
                                {
                                            customTag: "HELLO",
                                            customNumber: 187
                                        }""", OutputType.PLAIN_TEXT))
        );
        assertEquals(
                """
                        {
                            person: {
                                name: "Herbert"
                                address: {
                                    street: 'HerbertStreet',
                                    streetNumber: 15
                                },
                                {
                                    customTag: "HELLO",
                                    customNumber: 187
                                }
                            }
                        }
                        """,
                result
        );
    }

    @Test
    void parseRequestBodyMultipleParameters() {
        String result = HttpHelper.parseRequestBody(
                """
                        {
                            name: '{{param1}}',
                            herbert: '{{par}m2}}',
                            peter: '{{pa$am3}}',
                            franz: '{{pa{}{m4}}',
                        }
                        """, Map.of(
                        "param1", new ParameterValue("result1", OutputType.PLAIN_TEXT),
                        "par}m2", new ParameterValue("result2", OutputType.PLAIN_TEXT),
                        "pa$am3", new ParameterValue("result3", OutputType.PLAIN_TEXT),
                        "pa{}{m4", new ParameterValue("result4", OutputType.PLAIN_TEXT)));
        assertEquals(
                """
                        {
                            name: 'result1',
                            herbert: 'result2',
                            peter: 'result3',
                            franz: 'result4',
                        }
                        """,
                result
        );
    }

    @Test
    void parseRequestBodyOnlyParameter() {
        String result = HttpHelper.parseRequestBody(
                "{{ parameter }}", Map.of(
                        "parameter",
                        new ParameterValue("test", OutputType.PLAIN_TEXT)));
        assertEquals(
                "test",
                result
        );
    }

    @Test
    void parseRequestUrlWorking() {
        String result = HttpHelper.parseRequestUrl("https://localhost:8080/param/{param1}/hello",
            Map.of("param1", new ParameterValue("result2", OutputType.PLAIN_TEXT)));
        assertEquals("https://localhost:8080/param/result2/hello", result);
    }

    @Test
    void parseRequestUrlWithWhitespaces() {
        String result = HttpHelper.parseRequestUrl("https://localhost:8080/param/{   param1    }/hello",
            Map.of("param1", new ParameterValue("result2", OutputType.PLAIN_TEXT)));
        assertEquals("https://localhost:8080/param/result2/hello", result);
    }

    @Test
    void parseRequestUrlMultipleParameters() {
        String result = HttpHelper.parseRequestUrl(
                "https://localhost:8080/param/{param1}/{parm2}/{pa$am3}/{param4}", Map.of(
                        "param1", new ParameterValue("result1", OutputType.PLAIN_TEXT),
                        "parm2", new ParameterValue("result2", OutputType.PLAIN_TEXT),
                        "pa$am3",new ParameterValue("result3", OutputType.PLAIN_TEXT),
                        "param4", new ParameterValue("result4", OutputType.PLAIN_TEXT)));
        assertEquals("https://localhost:8080/param/result1/result2/result3/result4", result);
    }

    @Test
    void parseRequestUrlOnlyParameter() {
        String result = HttpHelper.parseRequestUrl(
                "{ parameter }", Map.of(
                        "parameter", new ParameterValue("test", OutputType.PLAIN_TEXT)));
        assertEquals("test", result);
    }
}