package com.jk51.modules.official.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.official.YbRecruitment;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.official.service.YbRecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2017/11/8.
 */
@Controller
@RequestMapping("/official")
public class YbRecruitmentContorller {
    @Autowired
    YbRecruitmentService ybRecruitmentService;

    @RequestMapping(value = "/addRecruitment",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto add(YbRecruitment ybRecruitment){
        int num=ybRecruitmentService.add(ybRecruitment);
        if (num > 0) {
            return ReturnDto.buildSuccessReturnDto("添加成功");
        }else {
            return ReturnDto.buildFailedReturnDto("添加失败");
        }
    }

    @RequestMapping(value = "/queryRecruitmentList",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queruyList(YbRecruitment ybRecruitment, @RequestParam(required = true,defaultValue = "1") int pageNum,@RequestParam(required = false,defaultValue = "15") int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<YbRecruitment> list=ybRecruitmentService.queryList(ybRecruitment);
        PageInfo pageInfo=new PageInfo<>(list);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(value = "/queryRecruitmentById",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryOne(int id){
        YbRecruitment ybRecruitment=ybRecruitmentService.queryOne(id);
        if(Objects.nonNull(ybRecruitment)){
            return ReturnDto.buildSuccessReturnDto(ybRecruitment);
        }else {
            return ReturnDto.buildFailedReturnDto("未查到相关信息");
        }
    }
}
