package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.ClassifiedStatistic;
import com.jk51.model.account.models.FinancesStatistic;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.requestParams.AccountParams;
import com.jk51.model.account.requestParams.ClassifiedAccountParam;
import com.jk51.model.account.requestParams.PayDataImportParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/17
 * Update   :
 */
@Mapper
public interface ClassifiedStatisticMapper {

   int addClassifiedStatistic (@Param(value = "classifiedStatistic") ClassifiedStatistic classifiedStatistic );

   List<FinancesStatistic> getClassifiedList(@Param("classifiedAccountParam") ClassifiedAccountParam classifiedAccountParam);

   List<Map<String,Object>> getClassified(@Param("classifiedAccountParam") ClassifiedAccountParam classifiedAccountParam);

   int updateCheckedMoney(@Param(value = "classifiedStatistic") ClassifiedStatistic classifiedStatistic);

}
