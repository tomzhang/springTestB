package com.jk51.modules.clerkvisit.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.clerkvisit.BVisitDescWithDetail;
import com.jk51.modules.clerkvisit.service.BVisitDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/26.
 */
@Controller
@RequestMapping("/visitDesc")
public class BVisitDescController {
    @Autowired
    private BVisitDescService bVisitDescService;

    @RequestMapping("/queryVisitDetailList")
    @ResponseBody
    public ReturnDto queryVisitDetailList(HttpServletRequest request, @RequestParam(required = true, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "15") int pageSize){
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        PageHelper.startPage(page,pageSize);
        List<BVisitDescWithDetail> list=bVisitDescService.queryVisitDetailList(param);
        PageInfo pageInfo=new PageInfo<>(list);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

}
