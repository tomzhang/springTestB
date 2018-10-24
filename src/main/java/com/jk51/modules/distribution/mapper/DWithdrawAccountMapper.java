package com.jk51.modules.distribution.mapper;

import com.jk51.modules.distribution.result.DWithdrawAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-17 16:20
 * 修改记录:
 */
@Mapper
public interface DWithdrawAccountMapper {


    /**
     * 查询开户行
     * @param distributorId
     * @param siteId
     * @return
     */
    List<DWithdrawAccount> selectWithdrawAccountBySiteIdAndDistributorId( @Param("siteId") Integer siteId, @Param("distributorId") Integer distributorId);

    DWithdrawAccount selectWithdrawAccountByRecordIdAndDistributorId(@Param("recordId") Long recordId,@Param("siteId") Integer siteId, @Param("distributorId") Integer distributorId);

    Integer withdrawAccountAdd(com.jk51.model.distribute.DWithdrawAccount account);
    
    List<com.jk51.model.distribute.DWithdrawAccount> getAccountList(@Param("siteId") Integer siteId, @Param("distributorId") Integer distributorId);

}
