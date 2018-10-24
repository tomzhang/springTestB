package com.jk51.modules.treat.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.treat.MerchantVO;
import com.jk51.modules.treat.service.FewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("few")
@Controller
public class FewController {

    @Autowired
    private FewService fewService;

    @ResponseBody
    @PostMapping("selectshops")
    public Map selectMerchant(@RequestParam(required = false, value = "merchant_name") String merchant_name,
                              @RequestParam(required = false, value = "merchant_id") Integer merchant_id,
                              @RequestParam(required = false, value = "site_status") Integer site_status,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date create_time_start,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date create_time_end,
                              @RequestParam(required = false, value = "set_type") Integer set_type,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date thelast_time,
                              @RequestParam(required = false, value = "valuea") Integer valuea,
                              @RequestParam(required = false, value = "valueb") Integer valueb,
                              @RequestParam(required = true, defaultValue = "1") int pageNum,
                              @RequestParam(required = false, defaultValue = "15") int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<MerchantVO> merchantVOS = fewService.selectMerchantList(merchant_name, merchant_id, site_status, create_time_start, create_time_end, set_type,thelast_time, valuea, valueb);
        PageInfo<MerchantVO> pageInfo = new PageInfo<>(merchantVOS);
        Map result = new HashMap();
        result.put("result", pageInfo);
        return result;
    }

    /**
     * 51后台用户显示列表（查询统一接口）
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/selectAllYbManager")
    public Map<String, Object> selectAllYbManager(@RequestParam(value = "username", required = false) String username,
                                         @RequestParam(value = "realname", required = false) String realname,
                                         @RequestParam(value = "isActive", required = false) Integer isActive,
                                         @RequestParam(required = true, defaultValue = "1") int pageNum,
                                         @RequestParam(required = false, defaultValue = "10") int pageSize) {
        Map result = new HashMap();
        result.put("result", fewService.selectAllYbManager(username, realname, isActive));
        return result;
    }

    @ResponseBody
    @PostMapping(value = "/selectAllYbManagerPage")
    public Map<String, Object> selectAllYbManagerPage(@RequestParam(value = "username", required = false) String username,
                                                  @RequestParam(value = "realname", required = false) String realname,
                                                  @RequestParam(value = "isActive", required = false) Integer isActive,
                                                  @RequestParam(required = true, defaultValue = "1") int pageNum,
                                                  @RequestParam(required = false, defaultValue = "10") int pageSize) {
        Map result = new HashMap();
        result.put("result", fewService.selectAllYbManagerPage(username, realname, isActive, pageNum, pageSize));
        return result;
    }
}
