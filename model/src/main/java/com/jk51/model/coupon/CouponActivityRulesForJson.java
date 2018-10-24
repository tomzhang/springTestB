package com.jk51.model.coupon;

import java.util.List;
import java.util.Map;

/**
 * 优惠券活动字段rules的对应实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司  <br/>
 *
 * @author : zhutianqiong
 * @version : 2017-07-25
 * @since 2017-07-25
 */
public class CouponActivityRulesForJson {
    /**
     * 优惠券发送数量的标识:
     * <ul>
     *     <li>1 -> 只发一张优惠券</li>
     *     <li>2 -> 选择多张优惠券，随机发放其中一张</li>
     *     <li>3 -> 同时发放多张优惠券</li>
     *     <li>4 -> 营销活动，选择多张，指定发放一张</li>
     * </ul>
     */
    private int sendNumTag;

    /**
     * 活动下所包含的优惠券 <br/>
     * Map中所包含的数据如下 <br/>
     * <table>
     *     <tr>
     *         <td>key</td>
     *         <td>value</td>
     *         <td>存在的可能性</td>
     *     </tr>
     *     <tr>
     *         <td>ruleId</td>
     *         <td>Integer类型的数值或字符串</td>
     *         <td>一定存在</td>
     *     </tr>
     *     <tr>
     *         <td>amount</td>
     *         <td>Integer类型的数值或字符串</td>
     *         <td>一定存在</td>
     *     </tr>
     * </table>
     *
     * ps : amount的值只有在编辑或创建的时候才是正确的，其他时候的这个值是不可以用来判断优惠券规则的剩余数量的
     */
    private List<Map<String, Object>> rules;

    public int getSendNumTag() {
        return sendNumTag;
    }

    public void setSendNumTag(int sendNumTag) {
        this.sendNumTag = sendNumTag;
    }

    public List<Map<String, Object>> getRules() {
        return rules;
    }

    public void setRules(List<Map<String, Object>> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "CouponActivityRulesForJson{" +
                "sendNumTag=" + sendNumTag +
                ", rules=" + rules +
                '}';
    }
}
