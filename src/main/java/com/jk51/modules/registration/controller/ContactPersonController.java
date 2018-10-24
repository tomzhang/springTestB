package com.jk51.modules.registration.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.registration.models.ContactPerson;
import com.jk51.modules.registration.service.ConactPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * filename :com.jk51.modules.registration.controller.
 * author   :mqq
 * date     :2017/4/7
 * Update   :
 */
@RestController
@RequestMapping("contact")
public class ContactPersonController {
    private static  final Logger logger= LoggerFactory.getLogger(ContactPersonController.class);

    @Autowired
    private ConactPersonService  conactPersonservice;

    @RequestMapping("querryAllContactPerson")
    @ResponseBody
    public ReturnDto queryallContactPersion(@RequestBody ContactPerson person){
        if(person.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(person.getMemberId() == null){
            return ReturnDto.buildFailedReturnDto("会员id不能为空");
        }
        List<ContactPerson> list=this.conactPersonservice.queryAllContactPersonByMemberId(person);

        return ReturnDto.buildSuccessReturnDto(list);

    }


    @RequestMapping("updateContactPerson")
    @ResponseBody
    public ReturnDto updateContactPerson(@RequestBody ContactPerson person){
        if(person.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(person.getMemberId() == null){
            return ReturnDto.buildFailedReturnDto("会员memberid不能为空");
        }
        if(person.getId() == null){
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }
        int result=this.conactPersonservice.updateContactPerson(person);

        return ReturnDto.buildSuccessReturnDto(result);

    }

    @RequestMapping("delContactPerson")
    @ResponseBody
    public ReturnDto delContactPerson(@RequestBody ContactPerson person){
        if(person.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(person.getId() == null){
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        int result=this.conactPersonservice.deleteContactPerson(person);

        return ReturnDto.buildSuccessReturnDto(result);
    }

    @RequestMapping("addContactPerson")
    @ResponseBody
    public ReturnDto addContactPerson(@RequestBody ContactPerson person){
        if(person.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(person.getMemberId() == null){
            return ReturnDto.buildFailedReturnDto("会员memberid不能为空");
        }
        if(person.getName() == null){
            return ReturnDto.buildFailedReturnDto("name不能为空");
        }
        if(person.getIdcard() == null){
            return ReturnDto.buildFailedReturnDto("idCard不能为空");
        }
        if(person.getSex() == null){
            return ReturnDto.buildFailedReturnDto("sex不能为空");
        }
        if(person.getAge() == null){
            return ReturnDto.buildFailedReturnDto("age不能为空");
        }
        if(person.getMobile() == null){
            return ReturnDto.buildFailedReturnDto("mobile不能为空");
        }

        int result=this.conactPersonservice.addContactPerson(person);
        return ReturnDto.buildSuccessReturnDto(result);
    }

}
