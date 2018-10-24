package com.jk51.modules.appInterface.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.service.AppGoodsService;
import com.jk51.modules.appInterface.service.UsersService;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.appInterface.util.ResultMap;
import org.bouncycastle.cms.bc.BcKeyTransRecipientInfoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: app商品接口
 * 作者: yeah
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/goods")
public class AppGoodsController {

    @Autowired
    AppGoodsService appGoodsService;
    @Autowired
    UsersService usersService;

    /**
     * 1.通过条形码查询
     * 2.通过商品id查询
     * 3.条件查询
     *
     * @param
     * @return
     */
    @RequestMapping("/searchproducts")
    public Map<String, Object> searchGoods(@RequestBody Map<String, Object> body) {
        //解析token
        String accessToken = (String) body.get("authToken");
        Integer drug_type = (Integer) body.get("drug_type");
        String drug_name = (String) body.get("drug_name");
        Object pageNum = body.get("pageNum");
        Object pageSize = body.get("pageSize");
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        int site_id = authToken.getSiteId();//获取token中的site_id storeAdminId
        body.put("site_id", site_id);
        body.put("store_id", authToken.getStoreId());
        body.put("storeAdminId", authToken.getStoreAdminId());
        Map<String, Object> map = new HashMap<String, Object>();

        if (Objects.nonNull(pageNum) && Objects.nonNull(pageSize)) {
            PageHelper.startPage(Integer.valueOf(pageNum.toString()), Integer.valueOf(pageSize.toString()));
        }
        if (drug_type == 1 || drug_type == 3 || drug_type == 4 || drug_type == 5 || drug_type == 6 || drug_type == 7) {
            //1---根据条形码查询商品，条形码放在drug_name中
            //3---根据商品名模糊查询商品，商品名放在drug_name中
            //4---根据批准文号查询商品，批准文号放在drug_name中
            //5---根据商品编码查询商品，商品编码放在drug_name中
            //7---根据商品编码和条形码查询商品，商品编码放在drug_name中
            map = this.appGoodsService.queryGoodsListByCondition(body);
        } else if (drug_type == 2) {
            //查询商品详情 单个商品 goods_id在drug_name中
            map = this.appGoodsService.queryGoodsDetailByGoodId(Integer.parseInt(drug_name), site_id, authToken.getStoreId(), body);
        } else if (drug_type == 0) {
            //根据商品名称模糊查询 不管商品条形码是否存在，商品状态必须是上架状态
            map = this.appGoodsService.queryGoodsListByCondition(body);
        } else {
            return ResultMap.errorResult("参数有误");
        }
        if (Objects.nonNull(pageNum) && Objects.nonNull(pageSize)) {
            Map results = (Map) map.get("results");
            map.put("pageHelpResult", new PageInfo((List) (results.get("items"))));
        }
        return map;
    }

    /**
     * 加载没有条形码的商品记录
     *
     * @param body
     * @return
     */
    @RequestMapping("/searchNullBarCode")
    public Map<String, Object> searchNullBarCode(@RequestBody Map<String, Object> body) {
        //解析token
        String accessToken = (String) body.get("authToken");
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        Integer pageNum = Integer.valueOf(body.get("pageNum").toString());
        Integer pageSize = Integer.valueOf(body.get("pageSize").toString());
        int site_id = authToken.getSiteId();//获取token中的site_id
        body.put("site_id", site_id);
        body.put("store_id", authToken.getStoreId());
        PageHelper.startPage(pageNum, pageSize);
        return appGoodsService.queryNullBarCode(body);

    }


    /**
     * 更新商品条形码
     *
     * @param map
     * @return
     */
    @RequestMapping("/updateproducts")
    public Map<String, Object> updateBarCode(@RequestBody Map<String, Object> map) {
        String accessToken = (String) map.get("authToken");
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        return this.appGoodsService.updateBarCode(authToken.getSiteId(), map);
    }


    @RequestMapping("/addOrUpdateBarCode")
    public ReturnDto addOrUpdateBarCode(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String authToken = String.valueOf(parameterMap.get("authToken"));
        AuthToken authToken1 = usersService.parseAccessToken(authToken);
        parameterMap.put("siteId", authToken1.getSiteId());
        int result = 0;
        result = appGoodsService.updateGoodsBarCode(parameterMap);
        if (1 == result) {
            return ReturnDto.buildSuccessReturnDto(1);
        } else {
            return ReturnDto.buildFailedReturnDto("添加修改条形码失败!");
        }

    }

}
