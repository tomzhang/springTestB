package com.jk51.modules.registration.service;

import com.jk51.model.registration.models.ContactPerson;
import com.jk51.modules.registration.mapper.ContactPersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by mqq on 2017/4/7.
 */
@Service
public class ConactPersonService {


   @Autowired
   ContactPersonMapper contactPersonMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
   public int updateContactPerson(ContactPerson person){
       int result=0; //设置默认联系人
       if(null==person.getId()){
           result= 0;
           return result;
       }

       if(null==person.getMemberId()){
           result= 0;
           return result;
       }
       int a=0;

       //如果设置为默认联系人 其它的先设置成非默认
       if(null!=person.getIsDefault()&&person.getIsDefault()==1){
       ContactPerson updateToUnDefault=new ContactPerson();
       updateToUnDefault.setMemberId(person.getMemberId());
       updateToUnDefault.setSiteId(person.getSiteId());
       updateToUnDefault.setIsDefault(new Integer(0));
           a =this.contactPersonMapper.updateByMemberId(updateToUnDefault);
       }
       int b=this.contactPersonMapper.updateByPrimaryKeySelective(person);
       return a+b;
   }


    public List<ContactPerson> queryAllContactPersonByMemberId(ContactPerson record){
         return contactPersonMapper.queryAllContactPersonByMemberId(record);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int  deleteContactPerson(ContactPerson record){
        record.setUpdateTime(new Date());
        record.setIsDel(new Integer(1));
        return contactPersonMapper.updateByPrimaryKeySelective(record);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int  addContactPerson(ContactPerson record){
        //如果设置为默认联系人 其它的先设置成非默认
        if(null!=record.getIsDefault()&&record.getIsDefault()==1){
            ContactPerson updateToUnDefault=new ContactPerson();
            updateToUnDefault.setMemberId(record.getMemberId());
            updateToUnDefault.setSiteId(record.getSiteId());
            updateToUnDefault.setIsDefault(new Integer(0));
            updateToUnDefault.setUpdateTime(new Date());
            this.contactPersonMapper.updateByMemberId(updateToUnDefault);
        }else{
            record.setIsDefault(0);
        }
        ContactPerson Default=new ContactPerson();
        Default.setMemberId(record.getMemberId());
        Default.setSiteId(record.getSiteId());
        if(contactPersonMapper.queryAllContactPersonByMemberId(Default).isEmpty()){
            record.setIsDel(1);
        }
        record.setIsDel(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        return contactPersonMapper.insert(record);
    }

}
