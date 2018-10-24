package com.jk51.modules.logistics.controller;

import com.jk51.commons.message.OldStyle;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Address;
import com.jk51.modules.logistics.requestData.AddressRequire;
import com.jk51.modules.logistics.requestData.ModifyAddress;
import com.jk51.modules.logistics.requestData.SelectParam;
import com.jk51.modules.logistics.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/address")
@ResponseBody
public class AddressController {
    private static Logger logger = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    AddressService addressService;

    /**
     * @api {post} /address/getdefault 获取用户默认收货地址
     * @apiName 获取用户默认收货地址
     * @apiGroup address
     * @apiSchema (请求参数) {jsonschema=../schema/address.getdefault.req.json} apiParam
     * @apiSchema (响应内容) {jsonschema=../schema/address.getdefault.res.json} apiSuccess
     * @apiParamJson {file=../example/address.getdefault.req.json} 请求参数
     * @apiSuccessJson {file=../example/address.getdefault.res.json} 响应参数
     */
    @RequestMapping("/getdefault")
    @ResponseBody
    public String getDefault(@Valid @RequestBody SelectParam sp, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            Address address = addressService.findDefault(sp.getBuyerId(), sp.getSiteId());
            if (address == null) {
                throw new Exception("没有记录");
            }
            return render(address);
        } catch (RuntimeException re) {
            return render(1001, re.getMessage());
        } catch (Exception e) {
            return render(1000, e.getMessage());
        }

    }

    @RequestMapping("/list")
    public String list(@Valid @RequestBody SelectParam sp, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            List<Address> ads = addressService.list(sp.getBuyerId(), sp.getSiteId());
            if (ads == null && ads.size() == 0) {
                throw new Exception("没有查询到数据");
            }

            Map map = new HashMap();
            map.put("items", ads);
            return render(map);
        } catch (RuntimeException re) {
            return render(1001, re.getMessage());
        } catch (Exception e) {
            return render(1000, e.getMessage());
        }
    }

    @RequestMapping("/findOne")
    public String findOne(@Valid @RequestBody AddressRequire addressRequire, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            Address address = addressService.findOne(addressRequire);
            if (address == null) {
                throw new Exception("没有该记录");
            }
            return render(address);
        } catch (RuntimeException re) {
            return render(1101, re.getMessage());
        } catch (Exception e) {
            return render(1100, e.getMessage());
        }
    }

    /**
     * @api {post} /address/add 添加收货地址
     * @apiName 添加收货地址
     * @apiGroup address
     * @apiSchema (请求参数) {jsonschema=../schema/address.add.req.json} apiParam
     * @apiSchema (响应内容) {jsonschema=../schema/address.add.res.json} apiSuccess
     * @apiParamJson {file=../example/address.update.req.json} 请求参数
     * @apiSuccessJson {file=../example/address.update.res.json} 响应参数
     */
    @RequestMapping("/add")
    public String add(@Valid @RequestBody Address address, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            if (StringUtil.isEmpty(address.getBuyerId())) {
                throw new RuntimeException("buyerId不能为空");
            }
            if (StringUtil.isEmpty(address.getSiteId())) {
                throw new RuntimeException("siteId不能为空");
            }
            int addressId = addressService.add(address);
            Map map = new HashMap();
            map.put("address_id", addressId);
            AddressRequire addressRequire=new AddressRequire();
            addressRequire.setAddressId(address.getAddressId());
            addressRequire.setSiteId(address.getSiteId());
            //addressRequire.setBuyerId(address.getBuyerId());
            Address addressNew = addressService.findOne(addressRequire);
            map.put("addressNew", addressNew);
            return render(map);
        } catch (RuntimeException re) {
            return render(1101, re.getMessage());
        } catch (Exception e) {
            return render(1100, e.getMessage());
        }
    }

    /**
     * @api {post} /address/update 更新收货地址
     * @apiName 更新收货地址
     * @apiGroup address
     * @apiParamJson {file=../example/address.update.req.json} 请求参数
     * @apiSuccessJson {file=../example/address.update.res.json} 响应参数
     */
    @RequestMapping("/update")
    public String update(@Valid @RequestBody ModifyAddress modifyAddress, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            boolean isUpdate = addressService.update(modifyAddress);
            if (!isUpdate) {
                throw new Exception("更新失败");
            }
            Map map = new HashMap();
            map.put("msg", "更新成功");
            AddressRequire addressRequire=new AddressRequire();
            addressRequire.setAddressId(modifyAddress.getAddressId());
            addressRequire.setSiteId(modifyAddress.getSiteId());
            //addressRequire.setBuyerId(modifyAddress.getBuyerId());
            Address addressNew = addressService.findOne(addressRequire);
            map.put("addressNew", addressNew);
            return render(map);
        } catch (RuntimeException re) {
            return render(1101, re.getMessage());
        } catch (Exception e) {
            return render(1100, e.getMessage());
        }

    }

    /**
     * @api {post} /address/setdefault 设置默认收货地址
     * @apiName 设置默认收货地址
     * @apiGroup address
     * @apiParamJson {file=../example/address.update.req.json} 请求参数
     * @apiSuccessJson {file=../example/address.update.res.json} 响应参数
     */
    @RequestMapping("/setdefault")
    public String setDefault(@Valid @RequestBody AddressRequire addressRequire, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            boolean isChanged = addressService.setDefault(addressRequire);
            if (!isChanged) {
                throw new Exception("设置失败");
            }
            Map map = new HashMap();
            map.put("msg", "设置成功");
            return render(map);
        } catch (RuntimeException re) {
            return render(1101, re.getMessage());
        } catch (Exception e) {
            return render(1100, e.getMessage());
        }
    }

    /**
     * @api {post} /address/delete 删除收货地址
     * @apiName 删除收货地址
     * @apiGroup address
     * @apiParamJson {file=../example/address.update.req.json} 请求参数
     * @apiSuccessJson {file=../example/address.update.res.json} 响应参数
     */
    @RequestMapping("/delete")
    public String delete(@Valid @RequestBody AddressRequire addressRequire, BindingResult bindingResult,HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            hasErrors(bindingResult);
            boolean isDelete = addressService.deleteNotDefault(addressRequire);

            if (!isDelete) {
                throw new Exception("删除失败");
            }
            Map map = new HashMap();
            map.put("msg", "删除成功");
            return render(map);
        } catch (RuntimeException re) {
            return render(1101, re.getMessage());
        } catch (Exception e) {
            return render(1100, e.getMessage());
        }
    }

    protected void hasErrors(BindingResult bindingResult) throws RuntimeException {
        if (bindingResult.hasErrors()) {
            FieldError fe = bindingResult.getFieldError();
            String err = fe.getField() + fe.getDefaultMessage();

            throw new RuntimeException(err);
        }
    }

    protected String render(Object obj) {
        return OldStyle.render(obj);
    }

    protected String render(int code, String msg) {
        return OldStyle.render(code, msg);
    }

    protected String render(String successTips) {
        Map result = new HashMap();
        result.put("code", 0);
        result.put("msg", successTips);
        return OldStyle.render(result);
    }
}
