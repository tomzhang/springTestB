package com.jk51.modules.registration.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.registration.models.ServceUseDetail;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/4/19.
 */
@Service
public class ServceUseDetailServce {
    private static final Logger log = LoggerFactory.getLogger(ServceUseDetailServce.class);
    @Autowired
    private ServceUseDetailMapper servceUseDetailMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto updateServceUser(ServceUseDetail servceUseDetail) {
       String[] params=servceUseDetail.getServUserIds().split(":");
        ServceUseDetail servceUseDetailTo=null;
        try {
           if(null!=params&&params.length>0){
               for(int a=0;a<params.length;a++){
                   servceUseDetailTo=new ServceUseDetail();
                   servceUseDetailTo.setSiteId(servceUseDetail.getSiteId());
                   servceUseDetailTo.setId(Integer.parseInt(params[a]));
                   servceUseDetailTo.setStatus(1);
                   servceUseDetailMapper.updateByPrimaryKeySelective(servceUseDetailTo);
               }
           }
        } catch (Exception e) {
            log.error("site :[{}],create template error ,parameter:[{}] exception:[{}] ", servceUseDetail.getSiteId(),
                    servceUseDetail.toString(), e);
            return ReturnDto.buildFailedReturnDto("取消排班失败，原因：" + e);
        }

        return ReturnDto.buildSuccessReturnDto("create template success");
    }
}
