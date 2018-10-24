package com.jk51.modules.store.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.order.MemberListParam;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.persistence.mapper.SBMemberInfoMapper;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.persistence.mapper.SYbMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 门店后台查询用户退款列表
 * 作者: zhangkuncheng
 * 创建日期: 2017/5/17
 * 修改记录:
 */
@Service
public class BMemberService {

    public static final Logger logger = LoggerFactory.getLogger(SRefundService.class);

    @Autowired
    private SBMemberMapper bMemberMapper;
    @Autowired
    private SBMemberInfoMapper bMemberInfoMapper;
    @Autowired
    private SYbMemberMapper ybMemberMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private CouponSendService couponSendService;

    /**
     * 获取会员列表
     *
     * @param memberParam
     * @return
     * @throws Exception
     */
    public PageInfo<Map<String, Object>> getMemberMapList(MemberListParam memberParam) throws Exception {
        PageHelper.startPage(memberParam.getPageIndex(), memberParam.getPageSize());//开启分页
        if (!StringUtil.isEmpty(memberParam.getInviteCode())) {
            String inv = memberParam.getInviteCode().substring(memberParam.getInviteCode().length() - 5, memberParam.getInviteCode().length());
            memberParam.setInviteCode(inv);
        }
        List<Map<String, Object>> list = bMemberMapper.getMemberMapList(handleMap(memberParam));
        return new PageInfo<>(list);
    }

    /**
     * 获取会员总数
     *
     * @param memberParam
     * @return
     * @throws Exception
     */
    public Integer getMemberCount(MemberListParam memberParam) throws Exception {
        return bMemberMapper.getMemberCount(handleMap(memberParam));
    }

    /**
     * 处理参数
     *
     * @param memberParam
     * @return
     * @throws Exception
     */
    private Map<String, Object> handleMap(MemberListParam memberParam) throws Exception {

        if (memberParam != null) {

            Map<String, Object> hashMap = new HashMap<String, Object>();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //分页数据处理
            Integer page_size = memberParam.getPageSize() > 0 ? memberParam.getPageSize() : 15;
            Integer pageIndex = memberParam.getPageIndex() > 0 ? memberParam.getPageIndex() : 1;
            Integer rowsIndex = (pageIndex - 1) * page_size;

            hashMap.put("page_size", page_size);
            hashMap.put("rows_index", rowsIndex);

            //封装查询条件
            hashMap.put("mobile", isExist(memberParam.getMobile()));
            hashMap.put("invite_code", isExist(memberParam.getInviteCode()));
            hashMap.put("site_id", memberParam.getSiteId());
            hashMap.put("store_id", memberParam.getStoreId());
            //查询条件日期处理
            Date date_start = null;
            Date date_end = null;

            if (StringUtil.isNotBlank(memberParam.getDateStart().trim())) {
                date_start = dateFormat.parse(memberParam.getDateStart().trim());

                hashMap.put("date_start", date_start);
            } else {
                hashMap.put("date_start", null);
            }

            if (StringUtil.isNotBlank(memberParam.getDateEnd().trim())) {
                date_end = dateFormat.parse(memberParam.getDateEnd().trim());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_end);
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                hashMap.put("date_end", calendar.getTime());
            } else {
                hashMap.put("date_end", null);
            }

            return hashMap;

        }

        return null;
    }

    /**
     * 判断参数是否存在
     *
     * @param object
     * @return
     */
    public Object isExist(Object object) {
        if (object != null) {
            if (object instanceof String) {
                return StringUtil.isBlank((String) object) ? null : ((String) object).trim();
            } else {
                return object;
            }
        } else {
            return null;
        }
    }

    //根据手机号查找
    public SBMember selectByPhoneNum(String mobile, Integer siteId) {
        return bMemberMapper.selectByPhoneNum(mobile, siteId);
    }

    //------------------------------------------------------
    //添加会员
    public int selectCountByMobile(String mobile) {
        return ybMemberMapper.selectCountByMobile(mobile);
    }

    public SYbMember selectByMobile(String mobile) {
        return ybMemberMapper.selectByMobile(mobile);
    }

    public void updateByPrimaryKeySelective(SYbMember sYbMember) {
        ybMemberMapper.updateByPrimaryKeySelective(sYbMember);
    }

    public Integer insertSelective(SYbMember sYbMember) {
        return ybMemberMapper.insertSelective(sYbMember);
    }
    /**/

    //添加会员扩展信息
    public Integer addMemberInfo(SBMemberInfo sBMemberInfo) {
        return bMemberInfoMapper.insertSelective(sBMemberInfo);
    }

    //添加ybmember
    public Integer addYbMember(SYbMember sYbMember) {
        return ybMemberMapper.insertSelective(sYbMember);
    }

    //校验手机号
    public Integer checkMobile(String moblie, Integer siteId, Integer storeId) {
        return bMemberMapper.selectByPhoneNumCount(moblie, siteId, storeId);
    }

    //获取member
    public SBMember getMembers(Integer memberId, Integer siteId, Integer storeId) {
        return bMemberMapper.getMember(memberId, siteId, storeId);
    }

    public SBMemberInfo getMemberInfo(Integer buyer_id, Integer siteId) {
        SBMemberInfo sbMemberInfo = bMemberInfoMapper.getMemberInfo(buyer_id, siteId);
        if (sbMemberInfo == null) {
            return null;
        }
        if (StringUtil.isEmpty(sbMemberInfo.getAddress())) {
            return sbMemberInfo;
        } else {
            Map<String, Object> areaIds = erpToolsService.getareaIds(sbMemberInfo.getAddress());
            if (StringUtil.isEmpty(areaIds.get("area"))) {
                return sbMemberInfo;
            } else {
                sbMemberInfo.setProvince(Integer.parseInt(areaIds.get("province").toString()));
                sbMemberInfo.setCity(Integer.parseInt(areaIds.get("city").toString()));
                sbMemberInfo.setArea(Integer.parseInt(areaIds.get("area").toString()));
                sbMemberInfo.setAddress(areaIds.get("address").toString());
            }
        }
        return sbMemberInfo;
    }

    public SBMemberInfo getMemberInfoTwo(Integer buyer_id, Integer siteId) {
        return bMemberInfoMapper.getMemberInfo(buyer_id, siteId);
    }

    public void updateMemberByMemberIds(SBMember sBMember) {
        bMemberMapper.updateMemberByMemberId(sBMember);
    }

    public void updateMemberByMemberIds1(SBMember sBMember, Integer buyerId) {
        Map param = new HashMap();
        param.put("idcard_number", sBMember.getIdcard_number());
        param.put("buyer_id", buyerId);

        ybMemberMapper.updateIdCardNumber(param);

        bMemberMapper.updateMemberByMemberId1(sBMember);
    }

    public void updateMemberByMemberIds2(SBMember sBMember) {
        bMemberMapper.updateMemberByMemberId2(sBMember);
    }


    public Integer updateMemberInfoByMemberId(SBMemberInfo sBMemberInfo) {
        //店员邀请码 storeAdminext_id
        if (!StringUtil.isEmpty(sBMemberInfo.getStoreAdminext_id())) {
            try {
                StoreAdminExt storeAdminExt = storeAdminExtMapper.selectByPrimaryKey(Integer.parseInt(sBMemberInfo.getStoreAdminext_id()), sBMemberInfo.getSite_id());
                if (storeAdminExt != null && !StringUtil.isEmpty(storeAdminExt.getClerk_invitation_code())) {
                    sBMemberInfo.setInvite_code(storeAdminExt.getClerk_invitation_code());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return bMemberInfoMapper.updateMemberInfoByMemberId(sBMemberInfo);
    }

    public Integer updateMemberInfoByMemberId1(SBMemberInfo sBMemberInfo) {
        //店员邀请码 storeAdminext_id
        if (!StringUtil.isEmpty(sBMemberInfo.getStoreAdminext_id())) {
            try {
                StoreAdminExt storeAdminExt = storeAdminExtMapper.selectByPrimaryKey(Integer.parseInt(sBMemberInfo.getStoreAdminext_id()), sBMemberInfo.getSite_id());
                if (storeAdminExt != null && !StringUtil.isEmpty(storeAdminExt.getClerk_invitation_code())) {
                    sBMemberInfo.setInvite_code(storeAdminExt.getClerk_invitation_code());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return bMemberInfoMapper.updateMemberInfoByMemberId1(sBMemberInfo);
    }

    public Integer updateMemberInfoByMemberId2(SBMemberInfo sBMemberInfo) {
        //店员邀请码 storeAdminext_id
        if (!StringUtil.isEmpty(sBMemberInfo.getStoreAdminext_id())) {
            try {
                StoreAdminExt storeAdminExt = storeAdminExtMapper.selectByPrimaryKey(Integer.parseInt(sBMemberInfo.getStoreAdminext_id()), sBMemberInfo.getSite_id());
                if (storeAdminExt != null && !StringUtil.isEmpty(storeAdminExt.getClerk_invitation_code())) {
                    sBMemberInfo.setInvite_code(storeAdminExt.getClerk_invitation_code());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return bMemberInfoMapper.updateMemberInfoByMemberId2(sBMemberInfo);
    }

    public List<Map<String, Object>> selectAll() {
        return bMemberMapper.selectAll();
    }

    /**
     * 添加会员
     *
     * @param member
     * @param memberInfo
     * @return
     */
    @Transactional
    public Integer addMember(SBMember member, SBMemberInfo memberInfo) throws Exception {

        //判断是否ybMember含有这个手机号
        if (ybMemberMapper.selectCountByMobile(member.getMobile()) > 0) {
            SYbMember ybMember = ybMemberMapper.selectByMobile(member.getMobile());

            //判断要添加会员的商户是否存在
            if (memberInfo.getSite_id() == null) {
                return 3;
            }
            //判断商户组是否存在
            else if (StringUtil.isBlank(ybMember.getbUsersarr())) {

                //更新Usersarr
                SYbMember newYbMember = new SYbMember();
                newYbMember.setMemberId(ybMember.getMemberId());
                newYbMember.setbUsersarr(member.getSite_id() + "");
                ybMemberMapper.updateByPrimaryKeySelective(newYbMember);

            }
            //判断是否已经含有这个商家
            else if (!ybMember.getbUsersarr().contains(member.getSite_id() + "")) {

                //更新Usersarr
                SYbMember newYbMember = new SYbMember();
                newYbMember.setMemberId(ybMember.getMemberId());
                newYbMember.setbUsersarr(ybMember.getbUsersarr() + ";" + member.getSite_id());
                ybMemberMapper.updateByPrimaryKeySelective(newYbMember);

            } else {
//                return 4;
            }

            member.setBuyer_id(ybMember.getMemberId());
            memberInfo.setMember_id(ybMember.getMemberId());

        } else {

            //封装ybmember，准备插入
            SYbMember ybMember = new SYbMember();

            ybMember.setMobile(member.getMobile());
            ybMember.setBirthday(memberInfo.getBirthday());
            ybMember.setName(member.getName());
            ybMember.setSex(member.getSex());
            ybMember.setIdcardNumber(member.getIdcard_number());
            ybMember.setArea(memberInfo.getArea() != null ? memberInfo.getArea()
                : memberInfo.getCity() != null ? memberInfo.getCity()
                : memberInfo.getArea() != null ? memberInfo.getArea() : 0);
            //注册来源: 110 (网站)，120（微信），130（app）,140 后台，9999（其它）
            ybMember.setReginSource(member.getMem_source());
            ybMember.setRegisterStores(member.getRegister_stores());
            ybMember.setbUsersarr(member.getSite_id() + "");

            //插入 ybMember
            Integer ybInt = ybMemberMapper.insertSelective(ybMember);
            SYbMember ybMemberSel = selectByMobile(member.getMobile());
            //获取memberId
            member.setBuyer_id(ybMemberSel.getMemberId());
            memberInfo.setMember_id(ybMemberSel.getMemberId());

        }
        // /插入会员信息
        int i = addMember(member);
        couponSendService.sendCoupon(member.getSite_id(), member.getMember_id());

        //校验info会员扩展信息表是否存在
        SBMemberInfo bMemberInfo = bMemberInfoMapper.getMemberInfo(member.getBuyer_id(), member.getSite_id());

        if (bMemberInfo == null) {
            //插入会员扩展信息
            return addMemberInfo(memberInfo);
        } else {
            //会员扩展信息已经存在不再录入
            return i;
        }

    }

    public Integer addMember(SBMember bMember) {

        Integer num = bMemberMapper.selectByPhoneNumCount(bMember.getMobile(), bMember.getSite_id(), null);
        if (num != 0) {
            return 4;
        }
        return bMemberMapper.insertSelective(bMember);
    }
    //获取member
    public SBMember getMembersBByOpenId( String openId, Integer siteId) {
        return bMemberMapper.selectByOpenId(openId, siteId);
    }
    //获取member
    public SBMember getMembersBByAliUserID( String openId, Integer siteId) {
        return bMemberMapper.getMembersBByAliUserID(openId, siteId);
    }

    public Integer getbMemberAll(Integer siteId) {
        return bMemberMapper.getbMemberAll(siteId);
    }
}


