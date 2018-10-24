package com.jk51.modules.registration.service;

import com.jk51.model.Goods;
import com.jk51.model.registration.models.ServceDoctor;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.registration.constants.RegistrationConstant;
import com.jk51.modules.registration.mapper.ServceDoctorMapper;
import com.jk51.modules.registration.mapper.ServceOrderMapper;
import com.jk51.modules.registration.request.DoctorAndGoodS;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/4/7.
 */

@Service
public class DoctorService {

    private  static  final Logger logger= LoggerFactory.getLogger(DoctorService.class);

    @Autowired
    private ServceDoctorMapper doctorMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ServceOrderMapper servceOrderMapper;

    public  int addDoctor(ServceDoctor doctor){
        return doctorMapper.insert(doctor);
    }

    public ServceDoctor getDoctorInfo(ServceDoctor doctor){
        return doctorMapper.selectByPrimaryKey(doctor.getId(),doctor.getSiteId());
    }

    public  List<ServceDoctor> findDoctorInfo(ServceDoctor doctor){
        return doctorMapper.findDoctorInfo(doctor);
    }

    public  int modifyDoctor(ServceDoctor doctor){
        return  doctorMapper.updateByPrimaryKeySelective(doctor);
    }

    public int deleteDoctor(Integer id,Integer siteId){
        return doctorMapper.deleteByPrimaryKey(id,siteId);
    }

    /**
     * @param doctor
     * @param goods
     * @return
     */
    @Transactional
    public int addDoctorAndGoods(ServceDoctor doctor, Goods goods){
       int res=goodsMapper.add(goods);
       doctor.setGoodsId(goods.getGoodsId());
        int result=doctorMapper.insertSelective(doctor);
        if (res==1&&result==1){
            return 1;
        }else {
            return 0;
        }
    }
    /**
     * @param doctor
     * @param goods
     * @return
     */
    @Transactional
    public int updateDoctorAndGoods(ServceDoctor doctor, Goods goods){

        Integer goodsId=doctorMapper.selectByPrimaryKey(doctor.getId(),doctor.getSiteId()).getGoodsId();
        goods.setGoodsId(goodsId);

        //医生停止或者商品下架
        if((doctor.getStatus()!=null||goods.getGoodsStatus()!=null)&&(doctor.getStatus()==1||goods.getGoodsStatus()==2)){
            servceOrderMapper.updateByGoodsId(2,goods.getSiteId(),goodsId);//服务暂停
        }
        //下架时间
        if(goods.getGoodsStatus()!=null&&goods.getGoodsStatus()==2){
            goods.setDelistTime(new Date());
        }
        boolean res=goodsMapper.updateByGoodsId(goods);
        int result=doctorMapper.updateByPrimaryKeySelective(doctor);
        if (res&&result==1){
            return 1;
        }else {
            return 0;
        }
    }

    /**
     *
     * @param doctor
     * @return
     */
    public List<DoctorAndGoodS> getDoctorAndGoods(DoctorAndGoodS doctor){

        List<DoctorAndGoodS> doctorAndGoodS=doctorMapper.selectDoctorAndGoods(doctor);
        return doctorAndGoodS;
    }

    /**
     * 上传医生头像
     * @param file
     * @return
     */
   /* public String uploadPic(MultipartFile file){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String resultStr = "";
        if (file != null){

            try {
                String fileName = file.getOriginalFilename();
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("image_base", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);
                builder.addTextBody("re_upload", "0");
                HttpEntity entity = builder.build();
                HttpPost httpPost = new HttpPost(RegistrationConstant.IMG_SERVER_URL);
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null){
                    String result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
                    JSONObject js= new JSONObject(result);
                    resultStr=js.getJSONObject("image").getString("url");
                }
                logger.info("图片上传完成,url:" + resultStr);
                return resultStr;
            }catch (Exception e){
                logger.error("图片上传失败",e);
            }finally {
                try {
                    if (httpClient != null){
                        httpClient.close();
                    }
                }catch (IOException e){
                    logger.error("", e);
                }
            }
        }
        return null;
    }*/

}
