package com.jk51.modules.distribution.controller;

import com.alibaba.fastjson.JSON;
import com.jk51.modules.distribution.request.DistributorModify;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DistributionControllerTest {
    private MockMvc mockMvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    DistributionController distributionController;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(distributionController).build();
    }

    public RequestBuilder postUpdateDistributor(DistributorModify dm) {
        String path = "/distribution/distributor/save";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(path)
                .contentType(contentType)
                .content(JSON.toJSONString(dm))
                .accept(MediaType.APPLICATION_JSON);

        return requestBuilder;
    }

    @Test
    public void updateDistributor() throws Exception {
        DistributorModify dm;
        dm = new DistributorModify();
        dm.setNote("123");

        RequestBuilder requestBuilder = postUpdateDistributor(dm);

        Supplier<org.hamcrest.Matcher> nullMsg = () -> isIn(new String[]{"siteId不能为空", "status不能为空", "id不能为空"});

        mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is("101")))
            .andExpect(jsonPath("$.message", nullMsg.get()));

        dm = new DistributorModify();
        dm.setSiteId(100030);

        requestBuilder = postUpdateDistributor(dm);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("101")))
                .andExpect(jsonPath("$.message", nullMsg.get()));

        dm = new DistributorModify();
        dm.setSiteId(100030);
        dm.setStatus(1);

        requestBuilder = postUpdateDistributor(dm);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("101")))
                .andExpect(jsonPath("$.message", nullMsg.get()));

        dm = new DistributorModify();
        dm.setSiteId(100030);
        dm.setStatus(1);
        dm.setId(1);

        requestBuilder = postUpdateDistributor(dm);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")))
                .andExpect(jsonPath("$.value.msg", is("success")));
    }
}