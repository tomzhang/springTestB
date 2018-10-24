package com.jk51.modules.registration.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.goods.PageData;
import com.jk51.model.registration.models.ServceTemplate;
import com.jk51.model.registration.models.ServceUseDetail;
import com.jk51.model.registration.models.ServiceTemplateFormatDate;
import com.jk51.model.registration.requestParams.MineClassesParams;
import com.jk51.model.registration.requestParams.TemplateParams;
import com.jk51.model.registration.requestParams.TemplateSonParams;
import com.jk51.modules.registration.mapper.ServceTemplateMapper;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import com.jk51.modules.registration.request.ServceOrderRequestParam;
import com.jk51.modules.registration.request.TemplateRequestParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.l;


/**
 * filename :com.jk51.modules.registration.goodsService.
 * author   :zw
 * date     :2017/4/7
 * Update   :
 */

@Service
public class TemplateService {
    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    @Autowired
    private ServceTemplateMapper servceTemplateMapper;
    @Autowired
    private ServceUseDetailMapper servceUseDetailMapper;

    /**
     * 生成模版
     *
     * @param templateParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto createTemplate(TemplateParams templateParams) {
        String str = checkCreateTemplateParams(templateParams);
        if (str != null) {
            return ReturnDto.buildFailedReturnDto(str);
        }
        // 生成模版编号
        String templateNo = createTemplateNo(templateParams.getSiteId(), templateParams.getStoreId());
        try {
            templateParams.getTemplateRule().forEach(p -> {
                servceTemplateMapper.insertSelective(buildTemplate(templateParams.getSiteId(), templateParams.getStoreId(), templateNo, p));
            });
        } catch (Exception e) {
            log.error("site :[{}],create template error ,parameter:[{}] exception:[{}] ", templateParams.getSiteId(),
                      templateParams.toString(), e);
            return ReturnDto.buildFailedReturnDto("创建医生模版失败，原因：" + e);
        }

        return ReturnDto.buildSuccessReturnDto("create template success");
    }

    /**
     * 修改模版
     *
     * @param templateParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto updateTemplate(TemplateParams templateParams) {
        String str = checkCreateTemplateParams(templateParams);
        if (str != null) {
            return ReturnDto.buildFailedReturnDto(str);
        }
        try {
            templateParams.getTemplateRule().forEach(p -> {
                servceTemplateMapper.updateByPrimaryKeySelective(updateTemplate(templateParams.getSiteId(), templateParams.getStoreId(),p));
            });
        } catch (Exception e) {
            log.error("site :[{}],create template error ,parameter:[{}] exception:[{}] ", templateParams.getSiteId(),
                    templateParams.toString(), e);
            return ReturnDto.buildFailedReturnDto("修改医生模版失败，原因：" + e);
        }

        return ReturnDto.buildSuccessReturnDto("create template success");
    }

    private String checkCreateTemplateParams(TemplateParams templateParams) {
        if (templateParams.getSiteId() == null) {
            return "site_id不能为空";
        }
        if (templateParams.getStoreId() == null) {
            return "store_id不能为空";
        }
        if (templateParams.getTemplateRule() == null) {
            return "模版排版时间不能为空";
        }
        return null;
    }

    public ServceTemplate buildTemplate(Integer siteId, Integer storeId, String templateNo, TemplateSonParams templateSonParams) {
        ServceTemplate servceTemplate = new ServceTemplate();
        servceTemplate.setSiteId(siteId);
        servceTemplate.setStoreId(storeId);
        servceTemplate.setTemplateNo(templateNo);
        servceTemplate.setStartTime(new Date(templateSonParams.getStartTime()));
        servceTemplate.setEndTime(new Date(templateSonParams.getEndTime()));
        servceTemplate.setAccountSource(templateSonParams.getAccountSource());
        return servceTemplate;
    }
    public ServceTemplate updateTemplate(Integer siteId, Integer storeId,TemplateSonParams templateSonParams) {
        ServceTemplate servceTemplate = new ServceTemplate();
        servceTemplate.setSiteId(siteId);
        servceTemplate.setStoreId(storeId);
        servceTemplate.setStartTime(new Date(templateSonParams.getStartTime()));
        servceTemplate.setEndTime(new Date(templateSonParams.getEndTime()));
        servceTemplate.setId(templateSonParams.getTemplateId());
        servceTemplate.setAccountSource(templateSonParams.getAccountSource());
///*        servceTemplate.setAccountSource(templateSonParams.getTemplateId());*/

        return servceTemplate;
    }

    private String createTemplateNo(Integer siteId, Integer storeId) {
        long timestamp = System.currentTimeMillis();
        String str = String.valueOf(siteId) + String.valueOf(storeId) + String.valueOf(timestamp);
        return str;
    }


    /**
     * 创建排班
     *
     * @param mineClassesParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto createMineClasses(MineClassesParams mineClassesParams) {

        String str = checkCreateMineClassesParams(mineClassesParams);
        if (str != null) {
            return ReturnDto.buildFailedReturnDto(str);
        }
        try {
                for (TemplateSonParams p : mineClassesParams.getTemplateRule()) {
                //1.判断商品的当天此时间点是否被排版，2.时间是否小于当天，
                if (!checkIsMineClasses(mineClassesParams.getSiteId(), mineClassesParams.getUseTime(), mineClassesParams.getGoodsId(),
                                        mineClassesParams.getTemplateNo(), p.getTemplateId())) {
                    return ReturnDto.buildFailedReturnDto("该医生此时间已排班或排班时间异常");
                }
                if (!checkMineClassesDate(mineClassesParams.getUseTime())) {
                    return ReturnDto.buildFailedReturnDto("当前时间不可排班");
                }
                //否则排班操作
                servceUseDetailMapper.insertSelective(buildUseDetail(mineClassesParams.getSiteId(), mineClassesParams.getStoreId(),
                                                                     mineClassesParams.getTemplateNo(),
                                                                     mineClassesParams.getGoodsId(), mineClassesParams.getUseTime(), p));
            }
        } catch (Exception e) {
            log.error("site :[{}],create mine classes error ,parameter:[{}] exception:[{}] ", mineClassesParams.getSiteId(),
                      mineClassesParams.toString(), e);
            return ReturnDto.buildFailedReturnDto("创建医生排班失败，原因：" + e);
        }
        return ReturnDto.buildSuccessReturnDto("create mine classes success");
    }

    //1.判断商品的当天此时间点是否被排版
    private boolean checkIsMineClasses(Integer siteId, long useTime, Integer goodsId, String templateNo, Integer templateId) {
        ServceTemplate servceTemplate = servceTemplateMapper.selectByNoAndId(templateNo, templateId);
        List<ServceUseDetail> servceUseDetails = servceUseDetailMapper.selectByUseTime(siteId, goodsId, new Date(useTime));
        if (servceTemplate == null) {
            log.error("没有找到此排版模版");
            return false;
        }
        if (servceUseDetails.isEmpty()) {
            return true;
        }
        for (ServceUseDetail obj : servceUseDetails) {
            ServceTemplate st = servceTemplateMapper.selectByNoAndId(obj.getTemplateNo(), obj.getMineClassesId());
            if (st == null) {
                log.error("查找排班模版异常");
                return false;
            }
            if (servceTemplate.getStartTime().getTime() > st.getEndTime().getTime() ||
                    servceTemplate.getEndTime().getTime() < st.getStartTime().getTime()) {
                return true;
            }
        }
        return false;
    }

    //检查日期是否大于今天
    private boolean checkMineClassesDate(long useTime) {
        long current = System.currentTimeMillis();//当前时间毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        if (useTime > zero) {
            return true;
        } else {
            return false;
        }
    }

    private String checkCreateMineClassesParams(MineClassesParams mineClassesParams) {
        if (mineClassesParams.getSiteId() == null) {
            return "site_id不能为空";
        }
        if (mineClassesParams.getStoreId() == null) {
            return "store_id不能为空";
        }
        if (mineClassesParams.getTemplateRule() == null) {
            return "模版排版时间不能为空";
        }
        if (StringUtils.isBlank(mineClassesParams.getTemplateNo())) {
            return "模版编号不能为空";
        }
        long useTime = 0;
        try {
            useTime = mineClassesParams.getUseTime();
        } catch (Exception e) {
            useTime = 0;
        }
        if (useTime == 0) {
            return "排班日期不能为空";
        }
        if (mineClassesParams.getGoodsId() == null) {
            return "商品id不能为空";
        }
        return null;
    }

    private ServceUseDetail buildUseDetail(Integer siteId, Integer storeId, String templateNo, Integer goodsId, long useTime,
                                           TemplateSonParams templateSonParams) {
        ServceUseDetail servceUseDetail = new ServceUseDetail();
        servceUseDetail.setSiteId(siteId);
        servceUseDetail.setStoreId(storeId);
        servceUseDetail.setTemplateNo(templateNo);
        servceUseDetail.setGoodsId(goodsId);
        servceUseDetail.setUseTime(new Date(useTime));
        servceUseDetail.setMineClassesId(templateSonParams.getTemplateId());
        servceUseDetail.setUseCount(templateSonParams.getAccountSource());
        servceUseDetail.setAccountSource(templateSonParams.getAccountSource());
        return servceUseDetail;
    }


    /**
     *
     * 获取医生一月的排班信息
     *
     * @param siteId
     * @param goodsId
     * @return
     */
    public ReturnDto selectMineClasses(Integer siteId, Integer goodsId) {
        Date startTime = getTimesMonthStart();
        Date endTime = getTimesMonthEnd();
        Map<String,Object> servceUseDetailsList = servceUseDetailMapper.selectAllMineClassesByGoodsId(siteId, goodsId,
                                                                                                         startTime, endTime);
        if (servceUseDetailsList == null) {
            return ReturnDto.buildFailedReturnDto("没有找到该医生的排班");
        }
        return ReturnDto.buildSuccessReturnDto(servceUseDetailsList);
    }


    //根据storeId查询模板
    public Map<String, List<ServceTemplate>> queryTemplateByStoreId(Integer storeId) {
        List<ServceTemplate> aa = servceTemplateMapper.queryTemplateStoreId(storeId);
        Map<String,List<ServceTemplate>> map = new HashMap<>();
        aa.stream().forEach(a -> {
            if(map.keySet().contains(a.getTemplateNo())){
                List<ServceTemplate> have = map.get(a.getTemplateNo());
                have.add(a);
                map.put(a.getTemplateNo(),have);
            }else{
                List<ServceTemplate> first = new ArrayList<>();
                first.add(a);
                map.put(a.getTemplateNo(),first);
            }

        });
        return map;

    }

    /**
     *
     * @param templateRequestParam
     * @return
     */

    //模板列表分页查询
    public PageInfo<?> queryAllTemplateByStore(TemplateRequestParam templateRequestParam) {
        PageHelper.startPage(templateRequestParam.getPageNum(), templateRequestParam.getPageSize());

        List<ServceTemplate> aa = servceTemplateMapper.queryAllTemplateByStore(templateRequestParam);
        List<ServceTemplate> secondList=null;
        List<Map<String,List<ServceTemplate>>> map = new ArrayList<Map<String,List<ServceTemplate>>>();
        PageInfo<?> list=new PageInfo<>(aa);
       if(!aa.isEmpty()){
           for(int a=0;a<aa.size();a++){
               Map<String,List<ServceTemplate>> thisMap=new HashMap<>();
               ServceTemplate thisPjo=aa.get(a);
               secondList=servceTemplateMapper.queryTemplateNo(thisPjo.getStoreId(),thisPjo.getTemplateNo());
               thisPjo.setTemPlateList(secondList);
               thisMap.put(thisPjo.getTemplateNo(),secondList);
               map.add(thisMap);
           }
       }
        PageInfo<?> result=new PageInfo<>(map);
        result.setEndRow(list.getEndRow());
        result.setTotal(list.getTotal());
        result.setStartRow(list.getStartRow());
        result.setEndRow(list.getEndRow());
        result.setSize(list.getSize());
        result.setPageSize(list.getPageSize());
        result.setPages(list.getPages());
        result.setPrePage(list.getPrePage());
        result.setNextPage(list.getNextPage());
        return result;

    }


    // 获得本月第一天0点时间
    public static Date getTimesMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }


    //根据storeId查询模板
    public Map<String, List<ServceTemplate>> queryTemplateNo(Integer storeId,String templateNo) {
        List<ServceTemplate> list = servceTemplateMapper.queryTemplateNo(storeId,templateNo);
        Map<String,List<ServceTemplate>> map = new HashMap<String,List<ServceTemplate>>();
        map.put("list",list);
        return map;

    }
}
