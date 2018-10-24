package com.jk51.modules.index.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.CloseIndexRecode;
import com.jk51.model.FirstWeight;
import com.jk51.model.StoreAdmin;
import com.jk51.model.Target;
import com.jk51.model.packageEntity.FirstWeightName;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.index.mapper.CloseIndexRecodeMapper;
import com.jk51.modules.index.mapper.FirstWeightMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.TargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 * 筛选店员
 */
@Service
public class ScreeningClerkService {

    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private CloseIndexRecodeMapper closeIndexRecodeMapper;
    @Autowired
    private CountIndexService countIndexService;
    @Autowired
    private FirstWeightMapper firstWeightMapper;
    @Autowired
    private TargetMapper targetMapper;
    @Autowired
    private FirstWeightService firstWeightService;
    @Autowired
    private TargetService targetService;
    @Autowired
    private StoreAdminService storeAdminService;
    @Autowired
    private CloseIndexRecodeService closeIndexRecodeService;

    /**
     *获取店员账户组成的String，用","隔开
     *
     * @param param
     *
     *
     * 二期取消指标排分筛选店员------只需要在商户后台设置店员的聊天权限和店员app在线，（一次发送消息限制在100人内，容联一次发送最多支持100人） 2017/6/12----Jay
     * */
    public List<StoreAdmin> getClerkId(Integer site_id){

        //String site_id = "100030";
        //查询一级权重
        //List<FirstWeight> firstWeightList = firstWeightService.getFirstWeightBySiteId(StringUtil.convertToInt(site_id));

        //List<Target> targetList = targetService.getTargetBySiteId(StringUtil.convertToInt(site_id));

        //查询site_id的所有店员及其指标总分（除亲密度外的）,手机号码
        return storeAdminService.findStoreAdminBySiteId(site_id);


        //根据site_id和sender查询关系密切性分------查询执行周期内的数据（比如关系密切性的定时周期为一周）
        //Date now = new Date();
       // Date before = DateUtils.getBeforeOrAfterDate(now,-7);
        //List<CloseIndexRecode> closeIndexRecodeList = closeIndexRecodeService.findCloseIndexRecodeBySenderAndSiteId(sender,StringUtil.convertToInt(site_id),now,before);



        /*if(StringUtil.isEmpty(storeAdminList)){
            return null;
        }*/

      /*  //遍历店员，查询与 sender 的亲密度指标分，如果没有记录，给初始分
        for(StoreAdmin storeAdmin:storeAdminList){

            //查询时间最大的亲密度记录

            CloseIndexRecode closeIndexRecode = getCloseIndexRecode(closeIndexRecodeList,storeAdmin);
            if(closeIndexRecode!=null){

                //设置亲密度指标值
                storeAdmin.setHistory_index(closeIndexRecode.getHistory_index());
                storeAdmin.setOrder_index(closeIndexRecode.getOrder_index());
            }else{

                //设置亲密度初始值
                initValue(storeAdmin, StringUtil.convertToInt(site_id));
            }
        }

        //获取店员List
        List<StoreAdmin> storeAdminLsit = getStoreAdminLsit(storeAdminList,firstWeightList,targetList,rsendNum);

        return storeAdminLsit;*/
    }


    //获取时间最大的一笔关系密切性指标值记录
    private CloseIndexRecode getCloseIndexRecode(List<CloseIndexRecode> closeIndexRecodeList,StoreAdmin storeAdmin){

        List<CloseIndexRecode> list = new LinkedList<CloseIndexRecode>();
        if(closeIndexRecodeList==null || closeIndexRecodeList.isEmpty()){
            return null;
        }

        for(CloseIndexRecode recode:closeIndexRecodeList){
            if(recode.getStoreadmin_id() == storeAdmin.getId()){
                list.add(recode);
            }
        }

        Collections.sort(list,new ComparatorCloseIndexRecode());

        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }


    //获取店员
    //rsendNum  重送的次数，决定筛选店员的数量，为0时筛选5名店员，为1时10名以此类推
    private List<StoreAdmin> getStoreAdminLsit(List<StoreAdmin> storeAdminList, List<FirstWeight> firstWeightList, List<Target> targetList,int resendNum){

        //计算总分
        for(StoreAdmin storeAdmin:storeAdminList){

            //关系密切性权重计算指标值
            double cloose_weight_value = countIndexService.getFirstWeightValue(firstWeightList, FirstWeightName.CLOSE);
            double intersection_weight_value  = countIndexService.getSecondWeigthValue(targetList,TargetName.INTERSECTION_INDEX);
            double order_num_weight_value  = countIndexService.getSecondWeigthValue(targetList,TargetName.ORDER_NUM_INDEX);
            double cloose_index = (storeAdmin.getHistory_index() * intersection_weight_value + storeAdmin.getOrder_index()+order_num_weight_value) * cloose_weight_value;

            storeAdmin.setAllIndex(storeAdmin.getCountIndex()+cloose_index);
        }

        //排序
        Collections.sort(storeAdminList,new ComparatorStoreAdmin());

        //筛选店员的数量
        int listSize = 5;
        if(resendNum!=0){
            listSize = listSize * resendNum;
        }
        if(storeAdminList.size()>listSize){
            return storeAdminList.subList(0,listSize);
        }else{
            return storeAdminList;
        }
    }

    //设置亲密度初始值
    private void initValue(StoreAdmin storeAdmin,int site_id){

        List<Target> targetList = targetService.getTargetBySiteId(site_id);
        Target intersectionTarget = countIndexService.getTarget(targetList, TargetName.INTERSECTION_INDEX);
        Target orderNumIndexTarget = countIndexService.getTarget(targetList, TargetName.ORDER_NUM_INDEX);
        storeAdmin.setHistory_index(StringUtil.isEmpty(intersectionTarget)?0:intersectionTarget.getInitial_value());
        storeAdmin.setOrder_index(StringUtil.isEmpty(orderNumIndexTarget)?0:orderNumIndexTarget.getInitial_value());
    }


    private final class ComparatorStoreAdmin implements Comparator<StoreAdmin>{

        @Override
        public int compare(StoreAdmin o1, StoreAdmin o2) {

            double indexa = o1.getAllIndex();
            double indexb = o2.getAllIndex();

            if(indexa<indexb){
                return 1;
            }else if(indexa==indexb){
                return 0;
            }else{
                return -1;
            }

        }
    }

    private final class ComparatorCloseIndexRecode implements Comparator<CloseIndexRecode>{

        @Override
        public int compare(CloseIndexRecode o1, CloseIndexRecode o2) {

            Date date1 = o1.getCreate_time();
            Date date2 = o2.getCreate_time();
            if(date1.after(date2)){
                return 1;
            }else if(date1.equals(date2)){
                return 0;
            }else{
                return -1;
            }

        }
    }

}
