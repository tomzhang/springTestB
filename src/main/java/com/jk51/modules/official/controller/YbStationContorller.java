package com.jk51.modules.official.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.official.YbStation;
import com.jk51.modules.official.service.YbStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2017/11/9.
 */
@Controller
@RequestMapping("/official")
public class YbStationContorller {

    @Autowired
    private YbStationService ybStationService;

    @RequestMapping(value = "/addStation",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto addStation(YbStation ybStation) {
        int num = ybStationService.add(ybStation);
        if (num > 0) {
            return ReturnDto.buildSuccessReturnDto("添加成功");
        } else {
            return ReturnDto.buildFailedReturnDto("添加失败");
        }
    }

    @RequestMapping(value = "/queryStationList",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryStation(YbStation ybStation, @RequestParam(required = true, defaultValue = "1") int pageNum, @RequestParam(required = false, defaultValue = "15") int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<YbStation> list=ybStationService.queryList(ybStation);
        PageInfo pageInfo=new PageInfo<>(list);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(value = "/updateStation",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto updateStation(YbStation ybStation){
        int num = ybStationService.update(ybStation);
        if (num > 0) {
            return ReturnDto.buildSuccessReturnDto("修改成功");
        } else {
            return ReturnDto.buildFailedReturnDto("修改失败");
        }
    }

    @RequestMapping(value = "/queryStationById",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryStation(int id){
        YbStation ybStation=ybStationService.queryStationById(id);
        if (Objects.nonNull(ybStation)) {
            return ReturnDto.buildSuccessReturnDto(ybStation);
        }else {
            return ReturnDto.buildFailedReturnDto("未查询到相关信息");
        }
    }
}
