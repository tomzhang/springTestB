package com.jk51.modules.appInterface.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.merchant.service.LabelSecondService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName AppQueryCustomer
 * @Description APP查询顾客列表
 * @Date 2018-04-04 16:29
 */
@Service
public class AppQueryCustomerService {


    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private LabelSecondService labelSecondService;

    /**
     * 根据基本信息查询顾客列表
     * @param parameterMap
     * @return
     */
    public ReturnDto queryCustomerByInfo(Map<String, Object> parameterMap) {
        Integer pageNum = Integer.valueOf(parameterMap.get("pageNum").toString());
        Integer pageSize = Integer.valueOf(parameterMap.get("pageSize").toString());
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> memList =  memberMapper.queryCustomerByInfo(parameterMap);
        Map<String,Object> result = new HashedMap();
        PageInfo pageInfo = new PageInfo(memList);
        result.put("memberCount",pageInfo.getTotal());//总记录数
        result.put("currentPage",pageInfo.getPageNum());
        result.put("pageSize",pageInfo.getPageSize());
        result.put("pageCount",pageInfo.getPages());//总页数
        result.put("members",memList);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 根据药品查询顾客列表
     * @param parameterMap
     * @return
     */
    @SuppressWarnings("all")
    public ReturnDto queryCustomerByDrug(Map<String, Object> parameterMap) {
        Integer pageNum = Integer.valueOf(parameterMap.get("pageNum").toString());
        Integer pageSize = Integer.valueOf(parameterMap.get("pageSize").toString());
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> memList =  memberMapper.queryCustomerByDrug(parameterMap);
        if (Objects.nonNull(memList) || memList.size() != 0) {
            Map<String,Object> result = new HashedMap();
            PageInfo pageInfo = new PageInfo(memList);
            result.put("memberCount",pageInfo.getTotal());
            result.put("currentPage",pageInfo.getPageNum());
            result.put("pageSize",pageInfo.getPageSize());
            result.put("totalPage",pageInfo.getPages());
            result.put("members",memList);
            return ReturnDto.buildSuccessReturnDto(result);
        }else {
            return ReturnDto.buildFailedReturnDto("没有查询到会员列表记录!");
        }
    }

    /**
     * 根据标签查询
     * @param parameterMap
     * @return
     */
    public ReturnDto getCustomerByLabel(Map<String, Object> parameterMap) {
        //查询顾客ID列表
        Map<String, Object> memberIdsToInsert = labelSecondService.getMemberIdsToInsert(parameterMap);
        String memberIds = String.valueOf(memberIdsToInsert.get("memberIds"));
        if (StringUtil.isEmpty(memberIds)) {
            return ReturnDto.buildFailedReturnDto("没有查询到会员记录!");
        }
        String[] split = memberIds.split(",");
        parameterMap.put("memberIds",split);
        Integer pageNum = Integer.valueOf(parameterMap.get("pageNum").toString());
        Integer pageSize = Integer.valueOf(parameterMap.get("pageSize").toString());
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> memberList = memberMapper.getCustomerByLabel(parameterMap);
        Map<String,Object> result = new HashedMap();
        PageInfo pageInfo = new PageInfo(memberList);
        result.put("memberCount",pageInfo.getTotal());
        result.put("currentPage",pageInfo.getPageNum());
        result.put("pageSize",pageInfo.getPageSize());
        result.put("totalPage",pageInfo.getPages());
        result.put("members",memberList);
        return ReturnDto.buildSuccessReturnDto(result);

    }
}
