package com.jk51.modules.registration.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.Goods;
import com.jk51.model.registration.models.ContactPerson;
import com.jk51.model.registration.models.ServceDoctor;
import com.jk51.modules.registration.mapper.ServceDoctorMapper;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import com.jk51.modules.registration.request.DoctorAndGoodS;
import com.jk51.modules.registration.request.DoctorServceOrderParam;
import com.jk51.modules.registration.request.SubscribeDetailRequestParam;
import com.jk51.modules.registration.service.ConactPersonService;
import com.jk51.modules.registration.service.DoctorService;
import com.jk51.modules.registration.service.ServceOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by twl on 2017/4/7.
 */

@RequestMapping("/doctor")
@Controller
public class DoctorController {

    private static  final Logger logger= LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ServceUseDetailMapper servceUseDetailMapper;

    @Autowired
    private ServceDoctorMapper servceDoctorMapper;

    @Autowired
    private ConactPersonService conactPersonservice;

    @Autowired
    ServceOrderService servceOrderService;

    /**
     * 根据id和siteId查询医生信息
     * @param doctor
     * @return
     */
    @RequestMapping("/getDoctorInfo")
    @ResponseBody
    public ReturnDto getDoctorInfo(@RequestBody ServceDoctor doctor){

        if (doctor.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if (doctor.getId() == null) {
            return ReturnDto.buildFailedReturnDto("Id为空");
        }
        ServceDoctor doct=doctorService.getDoctorInfo(doctor);
        return ReturnDto.buildSuccessReturnDto(doct);
    }

    /**
     * 根据多条件查询医生信息
     * @param doctor
     * @return
     */
    @RequestMapping("/findDoctorsInfo")
    @ResponseBody
    public ReturnDto findDoctorsInfo(@RequestBody ServceDoctor doctor){

        if (doctor.getSiteId() == null) {
           return ReturnDto.buildFailedReturnDto("siteId为空！");
        }

        List<ServceDoctor> docts=doctorService.findDoctorInfo(doctor);
        return ReturnDto.buildSuccessReturnDto(docts);
    }


    /**
     * 添加医生信息
     * @param doctor
     * @return
     */
    @RequestMapping("/addDoctor")
    @ResponseBody
    public ReturnDto addDoctor(@RequestBody ServceDoctor doctor){
        if (doctor.getSiteId() == null) {
          return ReturnDto.buildFailedReturnDto("siteId为空");
        }
         int result=doctorService.addDoctor(doctor);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 修改医生信息
     * @return
     */
    @RequestMapping("/modifyDoctor")
    @ResponseBody
    public ReturnDto modifyDoctor(@RequestBody ServceDoctor doctor){
        if (doctor.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if (doctor.getId() == null) {
            return  ReturnDto.buildFailedReturnDto("Id为空");
        }

        int res=doctorService.modifyDoctor(doctor);

        return ReturnDto.buildSuccessReturnDto(res);
    }

    /**
     *根据siteId和id删除医生信息
     * @param doctor
     * @return
     */
    @RequestMapping("/deleteDoctor")
    @ResponseBody
    public  ReturnDto deleteDoctor(@RequestBody ServceDoctor doctor){
        if (doctor.getSiteId() == null) {
            return  ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if (doctor.getId() == null) {
            return ReturnDto.buildFailedReturnDto("Id为空");
        }
        int  res=doctorService.deleteDoctor(doctor.getId(),doctor.getSiteId());
        return  ReturnDto.buildSuccessReturnDto(res);
    }

    /**
     * 获取医生和商品信息
     * @param doctor
     * @return
     */
    @RequestMapping(value="/getDoctorAndGoods",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getDoctorAndGoods(@RequestBody DoctorAndGoodS doctor){
        if (doctor.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId为空！");
        }
        try {
            logger.info("获取医生和商品信息："+doctor.toString());
            List<DoctorAndGoodS> doctorAndGoodS=doctorService.getDoctorAndGoods(doctor);
            return ReturnDto.buildSuccessReturnDto(doctorAndGoodS);
        }catch (Exception e){
            logger.error("获取信息失败：",e);
            return  ReturnDto.buildFailedReturnDto("获取信息失败！");
        }

    }

    /**
     * 添加医生和商品信息
     * @param doctor
     * @return
     */
    @RequestMapping(value="/addDoctorAndGoods",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto addDoctorAndGoods( DoctorAndGoodS doctor){

        try {
            logger.info("开始添加医生和商品信息："+doctor.toString());
            Goods goods=new Goods();
            ServceDoctor servceDoctor=new ServceDoctor();
            BeanUtils.copyProperties(doctor,goods);
            BeanUtils.copyProperties(doctor,servceDoctor);

            this.packagingGoods(goods,doctor);
            //数据校验
            String str=this.checkDate(goods);
            if (!StringUtils.isEmpty(str)) {
                return ReturnDto.buildFailedReturnDto(str);
            }
            int result=doctorService.addDoctorAndGoods(servceDoctor,goods);
            if (result==0){
                return ReturnDto.buildFailedReturnDto("添加信息失败！");
            }
            return ReturnDto.buildSuccessReturnDto("添加信息成功！");
        }catch (Exception e){
            logger.error("添加信息失败：",e);
            return  ReturnDto.buildFailedReturnDto("添加信息失败！");
        }

    }

    /**
     * 修改医生和商品信息：根据id和siteId
     * @param doctor
     * @return
     */
    @RequestMapping(value="/updateDoctorAndGoods",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto updateDoctorAndGoods(DoctorAndGoodS doctor){

        if (doctor.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if (doctor.getId() == null) {
            return ReturnDto.buildFailedReturnDto("Id为空");
        }
        try {
            logger.info("修改医生和商品信息："+doctor.toString());
            Goods goods=new Goods();
            ServceDoctor servceDoctor=new ServceDoctor();
            BeanUtils.copyProperties(doctor,goods);
            BeanUtils.copyProperties(doctor,servceDoctor);

            goods.setRemark(doctor.getGoodRemark());
            goods.setShopPrice(doctor.getMarketPrice());
            goods.setUpdateTime(new Date());
            goods.setDrugName(doctor.getGoodsTitle());

            //修改
            int result=doctorService.updateDoctorAndGoods(servceDoctor,goods);
            if (result==0){
                return  ReturnDto.buildFailedReturnDto("修改信息失败！");
            }
            return ReturnDto.buildSuccessReturnDto("修改信息成功！");
        }catch (Exception e){
            logger.error("修改信息失败：",e);
            return  ReturnDto.buildFailedReturnDto("修改信息失败！");
        }

    }

    /**
     * 封装补全商品缺失信息
     * @param goods
     * @param doctor
     */
    public  void packagingGoods(Goods goods,DoctorAndGoodS doctor){
        goods.setRemark(doctor.getGoodRemark());
        goods.setShopPrice(doctor.getMarketPrice());
        goods.setGoodsForpeople("110");
        goods.setListTime(new Date());
        goods.setDetailTpl(100);
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());
        goods.setDrugName(doctor.getGoodsTitle());
    }


    public String checkDate(Goods goods){
        StringBuffer str=new StringBuffer("");
        if (StringUtils.isEmpty(goods.getDrugName())){
            str.append("drugName：商品名称为空 ");
        }
        if (StringUtils.isEmpty(goods.getUserCateid())) {
            str.append("userCateid：商品分类为空 ");
        }
        if (goods.getMarketPrice()==null) {
            str.append("marketPrice：市场价格为空 ");
        }

        return  str.toString();
    }

    @RequestMapping(value="queryAllDoctorDetailByStore")
    @ResponseBody
    public  ReturnDto queryAllDoctorDetailByStore(SubscribeDetailRequestParam subscribeDetailRequestParam){
        PageInfo<?> pageInfo=null;
        try {
            pageInfo = this.servceOrderService.queryAllDoctorDetailByStore(subscribeDetailRequestParam);
        } catch (Exception e) {
            logger.error("获取列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("获取医生列表失败");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 获取预约商品列表
     * @param doctorServceOrderParam
     * @return
     */

    @RequestMapping(value="/queryDoctorForServceOrderMsg")
    @ResponseBody
    public ReturnDto queryDoctorForServceOrderMsg(@RequestBody DoctorServceOrderParam doctorServceOrderParam){
      if(doctorServceOrderParam.getSiteId()==null){
          return ReturnDto.buildFailedReturnDto("siteId为空");
      }

        Map<String,Object> map= new HashMap<String,Object>();
        map.put("siteId",doctorServceOrderParam.getSiteId());
        return  ReturnDto.buildSuccessReturnDto(this.servceDoctorMapper.queryDoctorForServceOrderMsg(map));

    }


    /**
     * 获取预约商品排班信息
     * @param doctorServceOrderParam
     * @return
     */
    @RequestMapping(value="/queryDoctorForServceOrderMsgDetail")
    @ResponseBody
    public ReturnDto queryDoctorForServceOrderMsgDetail(@RequestBody DoctorServceOrderParam doctorServceOrderParam){
        if(doctorServceOrderParam.getSiteId()==null){
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        if(doctorServceOrderParam.getGoodsId()==null){
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        }

        if(doctorServceOrderParam.getDoctorId()==null){
            return ReturnDto.buildFailedReturnDto("doctorId不能为空");
        }
        ServceDoctor doctor=new ServceDoctor();
        doctor.setSiteId(doctorServceOrderParam.getSiteId());
        doctor.setId(doctorServceOrderParam.getId());
        List<ServceDoctor> doctorlist=this.servceDoctorMapper.findDoctorInfo(doctor);
        if(!doctorlist.isEmpty()){
            doctor=doctorlist.get(0);
        }else{
            return ReturnDto.buildFailedReturnDto("没查到对应商品信息");
        }

        Map<String,Object> resultmap= new HashMap<String,Object>();
        Map<String,Object> parammap= new HashMap<String,Object>();
        parammap.put("siteId",doctorServceOrderParam.getSiteId());
        parammap.put("goodsId",doctorServceOrderParam.getGoodsId());
        resultmap.put("doctor",doctor);
        resultmap.put("valueList",this.servceUseDetailMapper.queryallServceOrderDetailList(parammap));
        return  ReturnDto.buildSuccessReturnDto(resultmap);

    }


    /**
     * 预约信息设置
     * @param doctorServceOrderParam
     * @return
     */
    @RequestMapping(value="/queryDoctorForServceOrderMsgDetailSubmit")
    @ResponseBody
    public ReturnDto queryDoctorForServceOrderMsgDetailSubmit(@RequestBody DoctorServceOrderParam doctorServceOrderParam){
         if(null==doctorServceOrderParam.getServceUseDetailid()){
             return ReturnDto.buildFailedReturnDto("servceUseDetailid不能为空");
         }

        if(null==doctorServceOrderParam.getDoctorId()){
            return ReturnDto.buildFailedReturnDto("doctorId不能为空");
        }


        if(null==doctorServceOrderParam.getSiteId()){
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        if(null==doctorServceOrderParam.getMemberId()){
            return ReturnDto.buildFailedReturnDto("memberId不能为空");
        }
        Map<String,Object> paramMap=new HashMap<String,Object>();
        paramMap.put("servceUseDetailid",doctorServceOrderParam.getServceUseDetailid());
        paramMap.put("doctorId",doctorServceOrderParam.getDoctorId());
        paramMap.put("siteId",doctorServceOrderParam.getSiteId());
        paramMap.put("memberId",doctorServceOrderParam.getMemberId());

        Map<String,Object> resultMap=new HashMap<String,Object>();
        List<ContactPerson> personList=null;
        List<ContactPerson> personListContact=null;
        ContactPerson personParam=new ContactPerson();

        //先查默认的就诊人
        personParam.setMemberId(new Integer(paramMap.get("memberId").toString()));
        personParam.setIsDefault(1);
        personList=this.conactPersonservice.queryAllContactPersonByMemberId(personParam);

        //如果没有默认就诊人  给返回一个非默认就诊人
        if(personList.isEmpty()){
            personParam.setIsDefault(0);
            personListContact=this.conactPersonservice.queryAllContactPersonByMemberId(personParam);
            if(!personListContact.isEmpty()){
                personList=new ArrayList<ContactPerson>();
                personList.add(personListContact.get(0));
            }
        }

        ServceDoctor doctor=new ServceDoctor();
        doctor.setSiteId(new Integer(paramMap.get("siteId").toString()));
        doctor.setId(new Integer(paramMap.get("doctorId").toString()));
        List<ServceDoctor> doctorlist=this.servceDoctorMapper.findDoctorInfo(doctor);
        doctor=doctorlist.get(0);

        Map<String,Object> detail=new HashMap<String,Object>();
        detail=this.servceUseDetailMapper.subscribMsgDetail(paramMap);
        resultMap.put("personList",personList);
        resultMap.put("doctor",doctor);
        resultMap.put("detail",detail);


        return  ReturnDto.buildSuccessReturnDto(resultMap);
    }




}

