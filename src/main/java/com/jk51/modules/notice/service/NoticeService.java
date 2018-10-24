package com.jk51.modules.notice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.Upgrade;
import com.jk51.modules.notice.mapper.UpgradeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-08-09 16:25
 * 修改记录:
 */
@Service
public class NoticeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(NoticeService.class);

    @Autowired
    UpgradeMapper upgradeMapper;

    public ReturnDto saveNotice(Upgrade upgrade){

        ReturnDto returnDto;
        try {
            if(upgradeMapper.insertSelective(upgrade) > 0){
                returnDto = ReturnDto.buildSuccessReturnDto("保存51jk公告成功");
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("保存51jk公告插入失败");
            }
        } catch (Exception e) {
            returnDto = ReturnDto.buildFailedReturnDto(e.getMessage());
        }
        return returnDto;
    }

    public PageInfo<Upgrade> getNoticeGroup(Map<String,Object> params) throws Exception{

        PageHelper.startPage(Integer.parseInt(params.get("pageNum").toString()),Integer.parseInt(params.get("pageSize").toString()));//开启分页

        List<Upgrade> noticeList = upgradeMapper.getUpgradeGroup(params);

        return new PageInfo<Upgrade>(noticeList);

    }

    public ReturnDto updateNotice(Upgrade upgrade){

        ReturnDto returnDto;
        try {
            if(upgradeMapper.updateByPrimaryKeySelective(upgrade) > 0){
                returnDto = ReturnDto.buildSuccessReturnDto("更新51jk公告成功");
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("更新51jk公告插入失败");
            }
        } catch (Exception e) {
            returnDto = ReturnDto.buildFailedReturnDto(e.getMessage());
        }
        return returnDto;
    }

    public ReturnDto deleteNotice(Map<String,Object> params){

        ReturnDto returnDto;
        try {
            if(upgradeMapper.deleteUpgradeByPrimaryKey(params) > 0){
                returnDto = ReturnDto.buildSuccessReturnDto("删除51jk公告id: " + params.get("id") + "成功");
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("删除51jk公告id: " + params.get("id") + "失败");
            }
        } catch (Exception e) {
            returnDto = ReturnDto.buildFailedReturnDto(e.getMessage());
        }
        return returnDto;
    }

    public Upgrade selectByCreateTime(int type){

        return upgradeMapper.selectByCreateTime(type);

    }

}
