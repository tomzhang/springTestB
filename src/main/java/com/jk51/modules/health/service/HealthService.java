package com.jk51.modules.health.service;

import com.github.pagehelper.PageHelper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.health.mapper.YbMemberHealthFileMapper;
import com.jk51.modules.persistence.mapper.SYbMemberMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-11-20
 * 修改记录:
 */
@Service
public class HealthService {
    @Autowired
    YbMemberHealthFileMapper ybMemberHealthFileMapper;
    @Autowired
    private SYbMemberMapper sybMemberMapper;

    public List<Map<String, Object>> healthList(Map<String, Object> param){
        Integer pageNum = Integer.parseInt(param.get("pageno").toString());
        Integer pageSize = Integer.parseInt(param.get("pageSize").toString());
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> result = ybMemberHealthFileMapper.healthList(param);
        return result;
    }

    public ReturnDto editHealthFile(Map<String, Object> param) {
        if(param==null || param.get("siteId")==null || param.get("id")==null || param.get("flag")==null)
            return ReturnDto.buildFailedReturnDto("缺少必要参数");

        ReturnDto returnDto = null;
        try {
            Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
            Integer id = Integer.parseInt(String.valueOf(param.get("id")));
            Integer flag = Integer.parseInt(String.valueOf(param.get("flag")));

            if(flag == 1) {
                String mobile = StringUtils.isNotBlank((String) param.get("mobile"))?(String) param.get("mobile"):null;
                String idCardNum = (String) param.get("idCardNum");
                if(StringUtils.isBlank(mobile) && StringUtils.isBlank(idCardNum))
                    return ReturnDto.buildFailedReturnDto("缺少必要参数");

                Integer memberId = null;
                if(mobile != null) {
                    if(!StringUtil.isMobileNO(mobile))
                        return ReturnDto.buildFailedReturnDto("手机号格式不正确");

                    SYbMember ybMember = sybMemberMapper.selectByMobile(mobile);
                    if(ybMember != null) {
                        memberId = ybMember.getMemberId();
                        if(StringUtils.isBlank(idCardNum) && StringUtils.isNotBlank(ybMember.getIdcardNumber())) {
                            idCardNum = ybMember.getIdcardNumber();
                        }
                    } else {
                        ybMember = new SYbMember();
                        ybMember.setMobile(mobile);
                        ybMember.setbUsersarr(siteId + "");
                        ybMember.setReginSource(150);
                        if (StringUtils.isNotBlank(idCardNum))
                            ybMember.setIdcardNumber(idCardNum);

                        sybMemberMapper.insertSelective(ybMember);
                        memberId = ybMember.getMemberId();
                    }
                }
                ybMemberHealthFileMapper.updateMobileOrIdCardNum(siteId, id, mobile, memberId, idCardNum);
            } else {
                return ReturnDto.buildFailedReturnDto("无效参数");
            }

            returnDto = ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            returnDto = ReturnDto.buildFailedReturnDto("操作异常");
        }
        return returnDto;
    }
}
