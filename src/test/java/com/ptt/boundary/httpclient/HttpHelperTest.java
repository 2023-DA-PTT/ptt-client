package com.ptt.boundary.httpclient;

import org.junit.jupiter.api.Test;

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
                        """, Map.of("param1", "result2")
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
                        """, Map.of("param1", "result2")
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
                                "personName", "Herbert",
                                "streetName", "HerbertStreet",
                                "streetNumber", "15",
                                "customObj", """
                                {
                                            customTag: "HELLO",
                                            customNumber: 187
                                        }""")
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
                        "param1", "result1",
                        "par}m2", "result2",
                        "pa$am3", "result3",
                        "pa{}{m4", "result4"));
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
                        "parameter", "test"));
        assertEquals(
                "test",
                result
        );
    }

    @Test
    void parseRequestUrlWorking() {
        String result = HttpHelper.parseRequestUrl("https://localhost:8080/param/{param1}/hello",
            Map.of("param1", "result2"));
        assertEquals("https://localhost:8080/param/result2/hello", result);
    }

    @Test
    void parseRequestUrlWithWhitespaces() {
        String result = HttpHelper.parseRequestUrl("https://localhost:8080/param/{   param1    }/hello",
            Map.of("param1", "result2"));
        assertEquals("https://localhost:8080/param/result2/hello", result);
    }

    @Test
    void parseRequestUrlMultipleParameters() {
        String result = HttpHelper.parseRequestUrl(
                "https://localhost:8080/param/{param1}/{parm2}/{pa$am3}/{param4}", Map.of(
                        "param1", "result1",
                        "parm2", "result2",
                        "pa$am3", "result3",
                        "param4", "result4"));
        assertEquals("https://localhost:8080/param/result1/result2/result3/result4", result);
    }

    @Test
    void parseRequestUrlOnlyParameter() {
        String result = HttpHelper.parseRequestUrl(
                "{ parameter }", Map.of(
                        "parameter", "test"));
        assertEquals("test", result);
    }
}