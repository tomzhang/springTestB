package com.jk51.modules.task.controller;

import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
@Rollback
public class ExaminationControllerTest {
    @Autowired
    ExaminationController examinationController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void find() throws Exception {
        Map request = new HashMap<String, Object>();
        request.put("id", 1);
        request.put("pageSize", 10);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/task/examination/find")
            .contentType(APPLICATION_JSON_UTF8_VALUE)
            .content(JacksonUtils.mapToJson(request));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is("000")))
            .andExpect(jsonPath("$.value").isArray())
        ;
    }

}
