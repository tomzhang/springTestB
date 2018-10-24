package com.jk51.modules.task.controller;

import com.gexin.rp.sdk.http.utils.ParamUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.JKHashMap;
import com.jk51.modules.task.service.OfflineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/16.
 */
@Controller
@RequestMapping("/task/offline")
public class OfflineController {
    private static final Logger log= LoggerFactory.getLogger(OfflineController.class);

    @Autowired
    private OfflineService offlineService;

    @RequestMapping("/commitOffline")
    @ResponseBody
    public ResponseEntity<ReturnDto> commitExam(@RequestBody Map<String, Object> exam) throws BusinessLogicException {
        try {
            JKHashMap result = offlineService.commitOffline(exam);
            return ResponseEntity.ok(ReturnDto.buildSuccessReturnDto(result));
        } catch (Exception e) {
            if (!(e instanceof BusinessLogicException)) {
                log.error("提交错误", e);
            }
            throw new BusinessLogicException(e.getMessage());
        }
    }
}
