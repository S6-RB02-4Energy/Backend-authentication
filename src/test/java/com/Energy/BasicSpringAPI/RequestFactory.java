package com.Energy.BasicSpringAPI;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RequestFactory {
    public static MockHttpServletRequestBuilder factoryRequest(String url) {
        return MockMvcRequestBuilders.get(url)
            .header("role", "CONSUMER")
            .header("id", "somefake1d");
    }

}
