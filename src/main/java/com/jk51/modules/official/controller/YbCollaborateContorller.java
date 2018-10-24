package com.jk51.modules.official.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.official.YbCollaborate;
import com.jk51.modules.official.service.YbCollaborateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2017/11/7.
 */
@Controller
@RequestMapping("/official")
public class YbCollaborateContorller {
    @Autowired
    YbCollaborateService ybCollaborateService;

    @RequestMapping(value = "/addCollaborate",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto add(YbCollaborate ybCollaborate, HttpServletRequest request){
        int num=ybCollaborateService.add(ybCollaborate);
        if(num > 0){
            return ReturnDto.buildSuccessReturnDto("添加成功");
        }else{
            return ReturnDto.buildFailedReturnDto("添加失败");
        }
    }

    @RequestMapping(value = "/updateCollaborate",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto update(YbCollaborate ybCollaborate, HttpServletRequest request){
        int num=ybCollaborateService.update(ybCollaborate);
        if(num > 0){
            return ReturnDto.buildSuccessReturnDto("修改成功");
        }else{
            return ReturnDto.buildFailedReturnDto("修改失败");
        }
    }

    /**
     * 合作列表分页查询
     * @param ybCollaborate
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/queryCollaborateList",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto query(YbCollaborate ybCollaborate, @RequestParam(required = true ,defaultValue = "1") int pageNum,@RequestParam(required = false,defaultValue = "15") int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<YbCollaborate> list=ybCollaborateService.queryList(ybCollaborate);
        PageInfo pageInfo=new PageInfo<>(list);
        return ReturnDto.buildSuccessReturnDto(pageInfo);

    }

    @RequestMapping(value = "/queryCollaborateById",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryById(int id){
        YbCollaborate ybCollaborate=ybCollaborateService.queryById(id);
        if (Objects.nonNull(ybCollaborate)) {
            return ReturnDto.buildSuccessReturnDto(ybCollaborate);
        }else {
            return ReturnDto.buildFailedReturnDto("未查询到相关信息");
        }
    }

}
