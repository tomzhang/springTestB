package com.jk51.modules.distribution.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.distribute.DDisappconfig;
import com.jk51.model.distribute.DWithdrawAccount;
import com.jk51.model.distribute.Distributor;
import com.jk51.model.distribute.OperationRecond;
import com.jk51.modules.distribution.request.DistributorModify;
import com.jk51.modules.distribution.request.WithdrawAccountAdd;
import com.jk51.modules.distribution.request.WithdrawRecordAdd;
import com.jk51.modules.distribution.service.DistributionService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Administrator on 2017/2/8.
 */
@RestController
@RequestMapping("/distribution")
public class DistributionController {

	@Autowired
	private DistributionService distributionService;
	/**
	 *查詢分銷商接口
	 *
	 * */
	@RequestMapping(value = "/distributor",method = RequestMethod.GET)
	public ReturnDto getDistribution(HttpServletRequest request, @RequestParam(required = true, defaultValue = "1") String pageNum, @RequestParam(required = false, defaultValue = "15") String pageSize){

		String siteId = request.getParameter("siteId");

		if(siteId == null || "".equals(siteId)){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}

		Map<String,Object> resultMap = new HashMap<String,Object>();

		Map<String,Object> paramsMap = ParameterUtil.getParameterMap(request);

		int page = Integer.parseInt(pageNum);
		int record = Integer.parseInt(pageSize);
		PageHelper.startPage(page,record);
		List<Distributor> distributorList =  distributionService.getAllDistributor(paramsMap);
		
		PageInfo<?> pageInfo = new PageInfo<>(distributorList);
		resultMap.put("page",pageInfo.getPageNum());
		resultMap.put("pageSize",pageInfo.getPageSize());
		resultMap.put("total",pageInfo.getTotal());
		resultMap.put("items",distributorList);

		System.out.println(resultMap);

		return ReturnDto.buildSuccessReturnDto(resultMap);
	}

	/**
	 * 查询分销商详情
	 * @param id
	 * @param siteId
	 */
	@RequestMapping(value = "/distributor/detail",method = RequestMethod.GET)
	@ResponseBody
	public ReturnDto distributorDetail(String id, String siteId){
		if(id == null || id.equals("")){
			return ReturnDto.buildFailedReturnDto("id 为空");
		}

		if(siteId == null || siteId.equals("")){
			return ReturnDto.buildFailedReturnDto("siteId 为空");
		}

		Map<String, Object> resultMap = new HashMap<String,Object>();

		Map<String, Object> distributorDetail = distributionService.getDistributorByID(Integer.parseInt(id),Integer.parseInt(siteId));

//		resultMap.put("result",distributorDetail);

		return ReturnDto.buildSuccessReturnDto(distributorDetail);

	}

	@RequestMapping(value = "/distributor/save",method = RequestMethod.POST)
	@ResponseBody
	public ReturnDto updateDistributor(@Valid @RequestBody DistributorModify dm, BindingResult bindingResult)
	{
		try {
			hasErrors(bindingResult);
			String note = Optional.ofNullable(dm.getNote()).orElse("");
			Map<String,String> resultMap = distributionService.updateDistributor(dm.getId(), dm.getSiteId(), dm.getStatus(), note);

			return ReturnDto.buildSuccessReturnDto(resultMap);
		} catch (Exception e) {
			return ReturnDto.buildFailedReturnDto(e.getMessage());
		}
	}

	/**
	 *根据分销商ID查询分销商状态修改记录(分页查询)12
	 *@param did
	 * @param pageNum
	 * @param pageSize
	 * */
	@RequestMapping("/operation/query")
	public ReturnDto queryDistributorChangeRecode(String did,String siteId, @RequestParam(required = true,defaultValue = "1")int pageNum, @RequestParam(required = false,defaultValue = "15")int pageSize ){
		if(did == null || "".equals(did)){
			return ReturnDto.buildFailedReturnDto("did为空");
		}

		if(siteId == null || "".equals(siteId)){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}

		Map<String,Object> resultMap = new HashMap<String,Object>();

		PageHelper.startPage(pageNum,pageSize);

		List<OperationRecond> recodeList = distributionService.getDistributorChangeRecord(Integer.parseInt(did),Integer.parseInt(siteId));

		PageInfo<?> pageInfo = new PageInfo<>(recodeList);

		resultMap.put("page",pageInfo.getPageNum());

		resultMap.put("pageSize",pageInfo.getPageSize());

		resultMap.put("total",pageInfo.getTotal());

		resultMap.put("items",recodeList);

		return ReturnDto.buildSuccessReturnDto(resultMap);
	}

	/**
	 * 查询d_distribution_store信息
	 * @param siteId
	 * @return
	 */
	@RequestMapping(value = "/getStores/{siteId}")
	public ReturnDto getStoresBySiteId(@PathVariable("siteId") Integer siteId){
		if(siteId == null || "".equals(siteId)){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}

		Map<String, Object> distributor = distributionService.getDistributorBySiteId(siteId);

		return ReturnDto.buildSuccessReturnDto(distributor);

	}

	@RequestMapping(value = "/setStores/{siteId}/{isOpen}")
	public ReturnDto setStores(@PathVariable("siteId") Integer siteId, @PathVariable("isOpen") Integer isOpen){
		if(siteId == null || "".equals(siteId)){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}

		if(isOpen == null || "".equals(isOpen)){
			return ReturnDto.buildFailedReturnDto("isOpen");
		}

		Integer result = distributionService.setDistributorStore(siteId,isOpen);
		System.out.println(result);

		String msg = "";
		if(result > 0){
			msg = "success";
		}else{
			msg = "failed";
		}

		return ReturnDto.buildSuccessReturnDto(msg);

	}

	protected void hasErrors(BindingResult bindingResult) throws RuntimeException {
		if (bindingResult.hasErrors()) {
			FieldError fe = bindingResult.getFieldError();
			String err = fe.getDefaultMessage();

			throw new RuntimeException(err);
		}
	}

	@RequestMapping("/selectBindUsers")
	@ResponseBody
	public Map<String,Object> selectBindUsers(){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,Object>> list =  distributionService.selectBindUsers();
		if(list.size() >0){
			resultMap.put("distributionList",list);
			resultMap.put("msg","success");
		}else{
			resultMap.put("msg","error");
		}
		return resultMap;
	}

	@RequestMapping("/checkUser")
	@ResponseBody
	public Map<String,Object> checkUser(HttpServletRequest request){
		Map<String,Object> param=ParameterUtil.getParameterMap(request);
		Map<String,Object> result=new HashedMap();
		//String username=request.getParameter("phone");
		Map<String,Object> distributor=distributionService.getDistributor(param);
		result.put("distributor",distributor);
		return result;

	}

	@RequestMapping("activateDistributor")
	@ResponseBody
	public Map<String,Object> activateDistributor(HttpServletRequest request){
		Map<String,Object> param=ParameterUtil.getParameterMap(request);
		Map<String,Object> resultMap=new HashMap<>();
		int id=Integer.parseInt(param.get("id").toString());
		int siteId=Integer.parseInt(param.get("siteId").toString());
		String result=distributionService.activateDistributor(id,siteId) > 0 ? "success":"error";
		resultMap.put("result",result);
		return resultMap;

	}

	@RequestMapping("/createDistributor")
	@ResponseBody
	public Map<String,Object> createDistributor(HttpServletRequest request){
		Map<String,Object> param=ParameterUtil.getParameterMap(request);
		Map<String,Object> result=new HashMap<>();
		String msg=distributionService.createDistributor(param) >0 ?"success":"error";
		result.put("msg",msg);
		return  result;
	}


	@GetMapping("/distributorInfo/{siteId}/{mobile}")
	public ReturnDto distributorInfo(@PathVariable("siteId") Integer siteId, @PathVariable("mobile") String mobile){
		if("".equals(siteId)){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}

		if("".equals(mobile)){
			return ReturnDto.buildFailedReturnDto("mobile为空");
		}

		Map<String,String> result = distributionService.getDistributorInfoByMobile(siteId,mobile);

		return ReturnDto.buildSuccessReturnDto(result);
	}

	@PostMapping("/withdraw/accountAdd")
	public ReturnDto withdrawAccountAdd(@Valid @RequestBody WithdrawAccountAdd account, BindingResult bindingResult)
	{
		try {
			hasErrors(bindingResult);

			if(Integer.parseInt(account.getType()) == 300 && ("".equals(account.getBandName()) || account.getBandName() == null)){
				return ReturnDto.buildFailedReturnDto("银行名称不能为空");
			}
			Integer result = distributionService.withdrawAccountAdd(account.getDistributorId(),account.getOwner(),account.getName(),account.getType(),account.getAccount(),account.getBandName());

			if(result > 0 ){
				return ReturnDto.buildSuccessReturnDto("成功");
			}else{
				return ReturnDto.buildSuccessReturnDto("失败");
			}

		} catch (Exception e) {
			return ReturnDto.buildFailedReturnDto(e.getMessage());
		}
	}
	@PostMapping("/withdraw/accountList")
	public ReturnDto withdrawAccountList(HttpServletRequest request,@RequestParam(required = true,defaultValue = "1") Integer pageNum,@RequestParam(required = false,defaultValue = "15") int pageSize){
		String siteId = request.getParameter("siteId");
		String distributorId = request.getParameter("distributorId");
		if(siteId == null || "".equals(siteId)){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}

		if(distributorId == null || "".equals(distributorId)){
			return ReturnDto.buildFailedReturnDto("distributorId为空");
		}
		PageHelper.startPage(pageNum,pageSize);
		List<DWithdrawAccount> accountList = distributionService.withdrawAccountList(Integer.parseInt(siteId),Integer.parseInt(distributorId));

		Map<String, Object> resultMap = new HashMap<>();
		PageInfo<?> pageInfo = new PageInfo<>(accountList);
		resultMap.put("page",pageInfo.getPageNum());
		resultMap.put("pageSize",pageInfo.getPageSize());
		resultMap.put("pages",pageInfo.getPages());
		resultMap.put("total",pageInfo.getTotal());
		resultMap.put("items",accountList);
		return ReturnDto.buildSuccessReturnDto(resultMap);
	}

	@PostMapping("/withdraw/recordAdd")
	public ReturnDto withdrawRecordAdd(@Valid @RequestBody WithdrawRecordAdd record, BindingResult bindingResult){
		try{
			hasErrors(bindingResult);
			String tradesIdString = buildTradeNo(record.getOwner()).toString().trim();

			Long tradesId = Long.parseLong(tradesIdString);

			record.setTradesId(tradesId);
			record.setType(1);
			record.setWithdrawFee(0);
			record.setTotalFee(0);
			record.setPayStatus(1);
			Integer remainingMoney = record.getRemainingMoney();
//			DWithdrawRecord dWithdrawRecord = JacksonUtils.getInstance().convertValue(record, DWithdrawRecord.class);
			Integer result = distributionService.withdrawRecordAdd(record);

			System.out.println(result);

			if(result > 0){
				return ReturnDto.buildSuccessReturnDto("成功");
			}else{
				return ReturnDto.buildSuccessReturnDto("失败");
			}

		}catch (Exception e){
			return ReturnDto.buildFailedReturnDto(e.getMessage());
		}
	}

	private String buildTradeNo(Integer siteId){
		String tradeNo = String.valueOf(siteId);
		if(tradeNo.length() < 5){
			tradeNo = tradeNo.concat("9");
		}

		Date date = new Date();
		System.out.println(String.valueOf(date.getTime()/1000));
		return tradeNo + String.valueOf(date.getTime()/1000)+ generateRandCode(1, 3);

	}

	/**
	 *
	 * @param type 1:数字,2:字母,其他:数字字母混合
	 * @param length 长度
	 * @return
	 */
	private String generateRandCode(Integer type, Integer length){
		String alpha = "ABCDEFGHJKMNPQRSTUVWXY";
		String number = "0123456789";
		List<String> charList = new ArrayList<>();
		String[] charArray;
		if (type == 1) {
			charArray = number.split("");

		}else if(type == 2){
			charArray = alpha.split("");

		}else{
			String numAlpha = number.concat(alpha);
			charArray = numAlpha.split("");

		}

		List<String> oroginList = Arrays.asList(charArray);

		for(int i = 0; i < length; i++){
			charList.add(charArray[i]);
		}
		charList.addAll(oroginList);
		Collections.shuffle(charList);

		return StringUtil.join(charList.toArray(),"").substring(0,length);
	}
	
	
	@GetMapping("/query")
	public ReturnDto myTeam(HttpServletRequest request){
		Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
		if(paramMap.get("siteId") == null || "".equals(paramMap.get("siteId"))){
			return ReturnDto.buildFailedReturnDto("siteId为空");
		}
		if(paramMap.get("distributorId") == null || "".equals(paramMap.get("distributorId"))){
			return ReturnDto.buildFailedReturnDto("distributorId为空");
		}
		
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Integer pageNum = (paramMap.get("pageNum") == null || "".equals(paramMap.get("pageNum")))?1:Integer.parseInt(paramMap.get("pageNum").toString());
		Integer pageSize = (paramMap.get("pageSize") == null || "".equals(paramMap.get("pageSize")))?15:Integer.parseInt(paramMap.get("pageSize").toString());
		PageHelper.startPage(pageNum,pageSize);

		if(paramMap.get("start") != null){
			paramMap.put("start",paramMap.get("start")+" 00:00:00");
		}

		if(paramMap.get("end") != null){
			paramMap.put("end",paramMap.get("end")+" 23:59:59");
		}
		
		List<Integer> distributorIds = distributionService.getDistributorId(paramMap);
		
		distributorIds.add(Integer.parseInt(paramMap.get("distributorId").toString()));
		
		paramMap.put("distributorIds",distributorIds);
		
		List<Map<String,Object>> distributorList = distributionService.getMyTeam(paramMap);
		
		System.out.println(distributorIds);
		PageInfo<?> pageInfo = new PageInfo<>(distributorList);
		resultMap.put("page",pageInfo.getPageNum());
		resultMap.put("pageSize",pageInfo.getPageSize());
		resultMap.put("pages",pageInfo.getPages());
		resultMap.put("total",pageInfo.getTotal());
		resultMap.put("items",distributorList);
		return ReturnDto.buildSuccessReturnDto(resultMap);
	}

	@GetMapping("/config/{siteId:\\d+}")
	@ResponseBody
	public ReturnDto getConfig(@PathVariable("siteId") Integer siteId) {
	    DDisappconfig disappconfig = distributionService.findConfig(siteId);

        if (Objects.nonNull(disappconfig)) {
            return ReturnDto.buildSuccessReturnDto(disappconfig);
        }

        return ReturnDto.buildFailedReturnDto("查询失败");
	}
	
}
