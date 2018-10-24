package com.jk51.modules.im.netease.dao;

import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.netease.NeteaseAccid;
import com.jk51.modules.im.netease.mapper.NeteaseAccidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@Service
public class NetaeaseAccidDao {

    @Autowired
    private NeteaseAccidMapper neteaseAccidMapper;

    private Logger logger = LoggerFactory.getLogger(NetaeaseAccidDao.class);

    @Async
    public void insertNeteaseAccid(NeteaseAccid neteaseAccid){
        try{
            neteaseAccidMapper.inseart(neteaseAccid);
        }catch (Exception e){
            logger.error("accid插入数据失败，报错信息：{}",ExceptionUtil.exceptionDetail(e));
        }
    }

    public NeteaseAccid getNeteaseAccid(String accid){

        return neteaseAccidMapper.findByAccid(accid);
    }
}
