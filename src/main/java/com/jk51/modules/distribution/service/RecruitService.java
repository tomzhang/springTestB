package com.jk51.modules.distribution.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.distribute.Recruit;
import com.jk51.model.distribute.RecruitRecord;
import com.jk51.modules.distribution.mapper.DistributorMapper;
import com.jk51.modules.distribution.mapper.RecruitMapper;
import com.jk51.modules.distribution.mapper.RecruitRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/9.
 */
@Service
public class RecruitService {

    @Autowired
    private RecruitMapper recruitMapper;
    @Autowired
    private RecruitRecordMapper recruitRecordMapper;
    @Autowired
    private DistributorMapper distributorMapper;

    /**
     * 根据主键ID获取招募信息
     * @param id
     * @return
     */
    public Recruit getRecruitById(String id){
        return recruitMapper.getRecruitById(id);
    }

    /**
     * 根据商家ID获取招募信息，原来拾取结果中的第一条
     * @param owner
     * @return
     */
    public List<Recruit> getRecruitListByOwner(String owner){
        return recruitMapper.getRecruitListByOwner(owner);
    }



    /**
     * 添加招募
     * @param recruit
     */
    @Transactional
    public Map<String, Object> insertRecruit(Recruit recruit){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("msg", "error");
        int i = recruitMapper.insertRecruit(recruit);
        if(i == 1){
            int j = insertRecruitRecord(recruit);//添加招募成功，添加对应记录信息
            if(j ==1){
                result.put("msg", "success");
            }
        }
        return result;
    }

    /**
     * 编辑招募
     * @param recruit
     */
    @Transactional
    public Map<String, Object> updateRecruit(Recruit recruit){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("msg", "error");
        int i = recruitMapper.updateRecruit(recruit);
        if(i == 1){
            int j = insertRecruitRecord(recruit);
            if(j ==1){
                result.put("msg", "success");
            }
        }
        return result;
    }

    /**
     * 添加招募成功，添加对应记录信息
     */
    public int insertRecruitRecord(Recruit recruit){
        int result = 0;
        if(recruit != null){
            RecruitRecord recruitRecord = new RecruitRecord();
            recruitRecord.setOwner(recruit.getOwner());
            recruitRecord.setIs_diposit(recruit.getIs_diposit());
            recruitRecord.setDeposit(recruit.getDeposit());
            recruitRecord.setRule(recruit.getRule());
            recruitRecord.setTotal_recruit(recruit.getTotal_recruit());
            recruitRecord.setAudit_mode(recruit.getAudit_mode());
            recruitRecord.setTemplate(recruit.getTemplate());
            result = recruitRecordMapper.insertRecruitRecord(recruitRecord);
        }
        return result;
    }


    /**
     * 获取商家下分销商总数 status = 1
     */
    public int getDistributorTotal(String owner, Integer status){
        return distributorMapper.getDistributorTotalByOwner(owner,status);
    }

    /**
     * 编辑招募信息
     * @param paramsMap
     * @return
     */
    public Map<String,Object> editRecruit(Map<String, Object> paramsMap) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        List<Recruit> recruitList =  getRecruitListByOwner(String.valueOf(paramsMap.get("owner")));
        if(!CollectionUtils.isEmpty(recruitList)){
            Recruit recruit = recruitList.get(0);

            if(paramsMap.get("template") != null && !"NULL".equals(paramsMap.get("template"))){
                recruit.setTemplate((String) paramsMap.get("template"));
            }else{
                recruit.setTemplate("");
            }
            if(paramsMap.get("audit_mode") != null){
                recruit.setAudit_mode(Integer.parseInt((String) paramsMap.get("audit_mode")));
            }
            if(paramsMap.get("total_recruit") != null){
                recruit.setTotal_recruit(Integer.parseInt((String) paramsMap.get("total_recruit")));
            }
            if(paramsMap.get("level") != null){
                recruit.setRule(String.valueOf(paramsMap.get("level")));
            }

            if(paramsMap.get("deposit") != null){
                recruit.setDeposit(Integer.parseInt((String) paramsMap.get("deposit")));
            }
            if(paramsMap.get("distribut_type") != null){
                recruit.setDistribut_type(Integer.parseInt((String) paramsMap.get("distribut_type")));
            }
            if(paramsMap.get("is_diposit") != null){
                recruit.setIs_diposit(Integer.parseInt((String) paramsMap.get("is_diposit")));
            }
            resultMap = updateRecruit(recruit);
        }else {
            Recruit recruit = new Recruit();
            if(paramsMap.get("owner") != null){
                recruit.setOwner(Integer.parseInt((String) paramsMap.get("owner")));
            }
            if(paramsMap.get("template") != null){
                recruit.setTemplate((String) paramsMap.get("template"));
            }
            if(paramsMap.get("audit_mode") != null){
                recruit.setAudit_mode(Integer.parseInt((String) paramsMap.get("audit_mode")));
            }
            if(paramsMap.get("total_recruit") != null){
                recruit.setTotal_recruit(Integer.parseInt((String) paramsMap.get("total_recruit")));
            }
            /*if(paramsMap.get("rule") != null){
                recruit.setRule((String) paramsMap.get("rule"));
            }*/
            if(paramsMap.get("level") != null){
                recruit.setRule(String.valueOf(paramsMap.get("level")));
            }
            if(paramsMap.get("deposit") != null){
                recruit.setDeposit(Integer.parseInt((String) paramsMap.get("deposit")));
            }
            if(paramsMap.get("distribut_type") != null){
                recruit.setDistribut_type(Integer.parseInt((String) paramsMap.get("distribut_type")));
            }
            if(paramsMap.get("is_diposit") != null){
                recruit.setIs_diposit(Integer.parseInt((String) paramsMap.get("is_diposit")));
            }
            resultMap = insertRecruit(recruit);
        }
        return resultMap;
    }

}
