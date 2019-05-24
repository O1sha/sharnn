package com.sai.lendperfect.common.security;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sai.lendperfect.logging.Logging;
import com.sai.lendperfect.application.util.CustomErr;
import com.sai.lendperfect.application.util.ErrConstants;
import com.sai.lendperfect.application.util.Helper;
import com.sai.lendperfect.application.util.ServiceProvider;
import com.sai.lendperfect.commodel.LpcomSecOwner;
import com.sai.lendperfect.commodel.LpcomSecurity;
import com.sai.lendperfect.commodel.LpcomAgriSecMapping;
import com.sai.lendperfect.commodel.LpcomCustInfo;
import com.sai.lendperfect.commodel.LpcomPropParty;
import com.sai.lendperfect.commodel.LpcomProposal;
import com.sai.lendperfect.commodel.LpcomSecFacMapping;

public class LpcomSecurityDataProvider {
	public Map<String, ?> getData(String dpMethod, HttpSession session, Map<?, ?> allRequestParams, Object masterData,ServiceProvider serviceProvider, Logging logging) {
		logging.setLoggerClass(this.getClass());
		Map<String,Object> responseHashMap = new HashMap<String, Object>();
		Map<String,Object> dataHashMap = new HashMap<String, Object>();
		Map <String,Object> lpcomSecurityHashMap=new HashMap<String,Object>();
		Map <String,Object> searchMap=new HashMap<String,Object>();
		List<Map<String, Object>> totalOwnerList = new ArrayList<Map <String, Object>>();
		long proposalNoValue = Long.parseLong(session.getAttribute("LP_COM_PROP_NO").toString());
		LpcomProposal lpcomProposal = serviceProvider.getLpcomProposalService().findByLpPropNo(proposalNoValue);
		Map <String,Object> lpcomSecOwnerDetHashMap=new HashMap<String,Object>();
		String propDenom  = session.getAttribute("propDenom").toString();
		responseHashMap.put("success", false);
		String strVertical=session.getAttribute("businessName").toString();
		try {
			
			if (dpMethod.equals("saveSecurityDetails")) {
				try {
					lpcomSecurityHashMap=(Map<String, Object>) allRequestParams.get("requestData");
					LpcomSecurity lpcomSecurity = new ObjectMapper().convertValue(lpcomSecurityHashMap.get("securityDetails"), new TypeReference<LpcomSecurity>() {});
					List<LpcomSecOwner> lpcomSecOwnerList = new ObjectMapper().convertValue(lpcomSecurityHashMap.get("ownerDetails"), new TypeReference<List<LpcomSecOwner>>() {});
				
					if(lpcomSecurity.getLsSecId() == 0)
					{
						long count = serviceProvider.getLpcomSecurityService().findSecCount((lpcomSecOwnerList.get(0).getLsoOwnerId()).longValue());
						String secId = (lpcomSecOwnerList.get(0).getLsoOwnerId()).toString()+"000";
						lpcomSecurity.setLsSecId(Long.parseLong(secId)+(count+1));
						lpcomSecurity.setLsSecCreatedOn(Helper.getSystemDate());
						lpcomSecurity.setLsCreatedBy(session.getAttribute("userid").toString());
	 					lpcomSecurity.setLsCreatedOn(Helper.getSystemDate());
	 					lpcomSecurity.setLsDelete("N");
					}
					lpcomSecurity.setLsChargedAmt(Helper.UnitsToActuals(propDenom, lpcomSecurity.getLsChargedAmt()));
					lpcomSecurity.setLsTotalSecurityVal(Helper.UnitsToActuals(propDenom, lpcomSecurity.getLsTotalSecurityVal()));
					lpcomSecurity.setLsNetSecValue(Helper.UnitsToActuals(propDenom, lpcomSecurity.getLsNetSecValue()));
					lpcomSecurity.setLsInsuredAmt(Helper.UnitsToActuals(propDenom, lpcomSecurity.getLsInsuredAmt()));
					lpcomSecurity.setLsModifiedBy(session.getAttribute("userid").toString());
 					lpcomSecurity.setLsModifiedOn(Helper.getSystemDate());	
					LpcomSecurity savedLpcomSecurity = serviceProvider.getLpcomSecurityService().saveLpcomSecurity(lpcomSecurity);
 					dataHashMap.put("securityDetails",savedLpcomSecurity);
 					
 					List<LpcomSecFacMapping> lpcomSecFacMappingList = serviceProvider.getLpcomSecFacMappingService().findByLpcomSecurityAndLpcomProposal(savedLpcomSecurity, lpcomProposal);
 					Iterator lpcomSecFacMappingItr = lpcomSecFacMappingList.iterator();
 					while(lpcomSecFacMappingItr.hasNext())
 					{
 						LpcomSecFacMapping lpcomSecFacMapping = (LpcomSecFacMapping) lpcomSecFacMappingItr.next();
 						lpcomSecFacMapping.setLsfmSecType(savedLpcomSecurity.getLsSecType());
 						lpcomSecFacMapping.setLsfmSecClassification(savedLpcomSecurity.getLsSecClassification());
 						lpcomSecFacMapping.setLsfmSecGrossVal(savedLpcomSecurity.getLsTotalSecurityVal());
 						lpcomSecFacMapping.setLsfmSecChargedAmt(savedLpcomSecurity.getLsChargedAmt());
 						lpcomSecFacMapping.setLsfmSecNetVal(savedLpcomSecurity.getLsNetSecValue());
 						serviceProvider.getLpcomSecFacMappingService().saveLpcomSecFacMapping(lpcomSecFacMapping);
 					}
 					
 					List<LpcomAgriSecMapping> LpcomAgriSecMappingLst=serviceProvider.getLpcomAgriSecMappingService().findByLpcomProposalAndLpcomSecurity(lpcomProposal,lpcomSecurity);
 					LpcomAgriSecMappingLst.forEach(agriSecurity->{
 						agriSecurity.setLasmSecNetVal(Helper.UnitsToActuals(propDenom,Helper.correctBigDecmial(savedLpcomSecurity.getLsNetSecValue())));
 						serviceProvider.getLpcomAgriSecMappingService().saveLpcomAgriSecMapping(agriSecurity);
 					});
 					
 					
 					List<LpcomSecOwner> lpcomSecOwnerListToBeDeleted = serviceProvider.getLpcomSecOwnerService().findByLpcomSecurity(lpcomSecurity);
 					if(lpcomSecOwnerListToBeDeleted != null)
 					{
 						Iterator lpcomSecOwnerListToBeDeletedItr = lpcomSecOwnerListToBeDeleted.iterator();
 	 					while(lpcomSecOwnerListToBeDeletedItr.hasNext())
 	 					{
 	 						LpcomSecOwner lpcomSecOwner = (LpcomSecOwner) lpcomSecOwnerListToBeDeletedItr.next();
 	 						serviceProvider.getLpcomSecOwnerService().deleteLpcomSecOwner(lpcomSecOwner);
 	 					}
 					} 
 					
 					BigDecimal ownerSno = new BigDecimal(0);
 					Iterator lpcomSecOwnerListItr = lpcomSecOwnerList.iterator();
 					while(lpcomSecOwnerListItr.hasNext())
 					{
 						LpcomSecOwner lpcomSecOwnerForItr = (LpcomSecOwner) lpcomSecOwnerListItr.next();
 						lpcomSecOwnerForItr.setLpcomSecurity(savedLpcomSecurity);
 						ownerSno = ownerSno.add(new BigDecimal(1));
 						lpcomSecOwnerForItr.setLsoOwnerSno(ownerSno);
 						if(lpcomSecOwnerForItr.getLsoRowId() == null)
 						{
 							lpcomSecOwnerForItr.setLsoCreatedBy(session.getAttribute("userid").toString());
 	 						lpcomSecOwnerForItr.setLsoCreatedOn(Helper.getSystemDate());	
 						}
						lpcomSecOwnerForItr.setLsoModifiedBy(session.getAttribute("userid").toString());
 						lpcomSecOwnerForItr.setLsoModifiedOn(Helper.getSystemDate());
 					}
 					
 					dataHashMap.put("ownerDetails",serviceProvider.getLpcomSecOwnerService().saveLpcomSecOwner(lpcomSecOwnerList));
					responseHashMap.put("success", true);
					responseHashMap.put("responseData",dataHashMap);
					
				} catch (Exception ex) {
					ex.printStackTrace();
					if (!dataHashMap.containsKey("errorData")) {
						logging.error("Provider : {} , Method : {} , ErrorMessage : {}", this.getClass().getName(),dpMethod, ex.getCause().getMessage());
						ex.printStackTrace();
						dataHashMap.put("errorData",new CustomErr(ErrConstants.invalidDataErrCode, ErrConstants.invalidDataErrMessage));
						
						responseHashMap.put("success", false);
						responseHashMap.put("responseData", dataHashMap);
					}
				}
			} else if (dpMethod.equals("getSecurityDetails")) {
				try {
					if(!(session.getAttribute("LP_COM_PROP_NO")).toString().equals("new"))
					{
					String secId = Helper.correctNull((String)allRequestParams.get("requestData"));
					if(!(secId.equals("")))
					{
						LpcomSecurity lpcomSecurity = serviceProvider.getLpcomSecurityService().findByLsSecId(Long.parseLong((String) allRequestParams.get("requestData")));
						if(lpcomSecurity != null)
						{
							Map<String,Object> lpcomSecurityMap = new ObjectMapper().convertValue(lpcomSecurity, Map.class);
							lpcomSecurityMap.put("lsChargedAmt", (Helper.ActualsToUnits(propDenom, lpcomSecurity.getLsChargedAmt())).toString());
							lpcomSecurityMap.put("lsTotalSecurityVal", (Helper.ActualsToUnits(propDenom, lpcomSecurity.getLsTotalSecurityVal())).toString());
						
							lpcomSecurityMap.put("lsNetSecValue", (Helper.ActualsToUnits(propDenom, lpcomSecurity.getLsNetSecValue())).toString());
						if(strVertical.equals("7")){
							 List<LpcomAgriSecMapping> LpcomAgriSecMappingLst=serviceProvider.getLpcomAgriSecMappingService().findByLpcomProposalAndLpcomSecurity(lpcomProposal,lpcomSecurity);
							 if(!LpcomAgriSecMappingLst.isEmpty()){
							  LpcomAgriSecMapping LpcomAgriSecMapping=LpcomAgriSecMappingLst.get(0);
							 if(lpcomSecurity.getLsSecClassification().compareTo(LpcomAgriSecMapping.getLasmSecClassification())==0){
								 lpcomSecurityMap.put("lsNetSecValue", (Helper.ActualsToUnits(propDenom, LpcomAgriSecMapping.getLasmSecNetVal())).toString());
							 }
							  
							 }
							
						}
							lpcomSecurityMap.put("lsInsuredAmt", (Helper.ActualsToUnits(propDenom, lpcomSecurity.getLsInsuredAmt())).toString());
							
							lpcomSecurityMap.put("lsAddress", (lpcomSecurity.getLsAddress().toString()));
							
							if(lpcomSecurity.getLsBgAson() != null)
								lpcomSecurityMap.put("lsBgAson", new SimpleDateFormat("dd/MM/yyyy").format(lpcomSecurity.getLsBgAson()));
							
							
							
							if(lpcomSecurity.getLsSecCreatedOn() != null)
								lpcomSecurityMap.put("lsSecCreatedOn", new SimpleDateFormat("dd/MM/yyyy").format(lpcomSecurity.getLsSecCreatedOn()));
							dataHashMap.put("securityDetails",lpcomSecurityMap);
							dataHashMap.put("lpcomSecOwners",serviceProvider.getLpcomSecOwnerService().findByLpcomSecurity(lpcomSecurity));
							responseHashMap.put("success", true);
							responseHashMap.put("responseData",dataHashMap);
							
							ArrayList<String> secAttachList = new ArrayList<String>();
							secAttachList.add("P");
							secAttachList.add("S");
							
							switch(String.valueOf(lpcomSecurity.getLsSecType()))
							{
								case "1":
									if(lpcomSecurity.getLpcomSecProperties().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;
								
								case "2":
									if(lpcomSecurity.getLpcomSecVehicleDets().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "3":
									if(lpcomSecurity.getLpcomSecFindocTrades().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "4":
									if(lpcomSecurity.getLpcomSecFindocNtrades().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "5":
									if(lpcomSecurity.getLpcomSecBankDeposits().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "6":
									if(lpcomSecurity.getLpcomSecJewelDets().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "7":
									if(lpcomSecurity.getLpcomSecPlntMcnries().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "8":
									if(lpcomSecurity.getLpcomSecFurnFixDets().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;

								case "9":
									if(lpcomSecurity.getLpcomSecGoodsDets().isEmpty())
										responseHashMap.put("secODEntry", "N");
									else
										responseHashMap.put("secODEntry", "Y");
								break;
								
								default:
									responseHashMap.put("secODEntry", "N");
								break;
							}
						}
					}
					
					List<LpcomPropParty> lpcomPropPartyList = serviceProvider.getLpcomPropPartyService().findByLpcomProposalAndLppBorrowerAndLppCustActiveOrderByLppCustId(lpcomProposal, "Y", "Y");
					Iterator lpcomPropPartyListItr = lpcomPropPartyList.iterator();
					while(lpcomPropPartyListItr.hasNext())
					{
						searchMap=new HashMap();
						LpcomPropParty lpcomPropPartyItr = (LpcomPropParty) lpcomPropPartyListItr.next();
						LpcomCustInfo lpcomCustInfo = serviceProvider.getLpcomCustInfoService().findByLciCustIdAndLciRecent(lpcomPropPartyItr.getLppCustId(),"Y");
						if(lpcomCustInfo != null)
						{
							searchMap.put("ownerName", lpcomCustInfo.getLciCustName().toString());
							searchMap.put("ownerId", lpcomCustInfo.getLciCustId().toString());
							List<Object[]> secFacList = serviceProvider.getLpcomSecFacMappingService().findOwnerMap(proposalNoValue, lpcomCustInfo.getLciCustId(), secId);
							if(secFacList.isEmpty())
								searchMap.put("secOwnerFacMapCheck", "N");
							else
								searchMap.put("secOwnerFacMapCheck", "Y");
							
							totalOwnerList.add(searchMap);							
						}
					}
					
					responseHashMap.put("totalOwnerList",totalOwnerList);
					responseHashMap.put("natureOfChargeList",serviceProvider.getLpmasListofvalueService().findByLlvHeaderAndLlvActiveOrderByLlvRowId("natureofcharge", "Y"));
					responseHashMap.put("Vertical", strVertical);
					}
				} catch (Exception ex) {
					if (!dataHashMap.containsKey("errorData")) {
						logging.error("Provider : {} , Method : {} , ErrorMessage : {}", this.getClass().getName(),dpMethod, ex.getMessage());
						dataHashMap.put("errorData",new CustomErr(ErrConstants.invalidDataErrCode, ErrConstants.invalidDataErrMessage));
						responseHashMap.put("success", false);
						responseHashMap.put("responseData", dataHashMap);
					}
				}
			} else if (dpMethod.equals("deleteOwner")) {
				try {
					
					
					lpcomSecOwnerDetHashMap=(Map<String, Object>) allRequestParams.get("requestData");
					LpcomSecOwner lpcomSecOwner = new ObjectMapper().convertValue(lpcomSecOwnerDetHashMap.get("ownerDetail"), new TypeReference<LpcomSecOwner>() {});
					long secId = Long.parseLong((lpcomSecOwnerDetHashMap.get("secId")).toString());
					
					
					
					serviceProvider.getLpcomSecOwnerService().deleteLpcomSecOwner(lpcomSecOwner);
					
					LpcomSecurity lpcomSecurity = new LpcomSecurity();
					lpcomSecurity = serviceProvider.getLpcomSecurityService().findByLsSecId(secId);
					
					List<LpcomSecOwner> lpcomSecOwnerList = serviceProvider.getLpcomSecOwnerService().findByLpcomSecurity(lpcomSecurity);
					
//					
//					LpcomSecOwner lpcomSecOwner = new ObjectMapper().convertValue(allRequestParams.get("requestData"), new TypeReference<LpcomSecOwner>() {});
//					
//					LpcomSecOwner savedLpcomSecOwner = serviceProvider.getLpcomSecOwnerService().findByLsoRowId(lpcomSecOwner.getLsoRowId());
//					LpcomSecurity lpcomSecurity = savedLpcomSecOwner.getLpcomSecurity();
//					
//					serviceProvider.getLpcomSecOwnerService().deleteLpcomSecOwner(lpcomSecOwner);
//					List<LpcomSecOwner> lpcomSecOwnerList = serviceProvider.getLpcomSecOwnerService().findByLpcomSecurity(lpcomSecurity);
					
					responseHashMap.put("success", true);
					dataHashMap.put("securityDetails",lpcomSecurity);
					dataHashMap.put("ownerDetails",lpcomSecOwnerList);
					responseHashMap.put("responseData",dataHashMap);
					
				} catch (Exception ex) {
					if (!dataHashMap.containsKey("errorData")) {
						logging.error("Provider : {} , Method : {} , ErrorMessage : {}", this.getClass().getName(),dpMethod, ex.getMessage());
						dataHashMap.put("errorData",new CustomErr(ErrConstants.invalidDataErrCode, ErrConstants.invalidDataErrMessage));
						responseHashMap.put("success", false);
						responseHashMap.put("responseData", dataHashMap);
					}
				}
			} 
			else if (dpMethod.equals("getSecClassificationSubType")) {
				try {
					
					BigDecimal secId=new BigDecimal(Long.parseLong(allRequestParams.get("requestData").toString()));
					dataHashMap.put("secClassificationSubType",serviceProvider.getLpstpSecurityService().findByLsParentIdAndLsActive(secId, "Y"));
					responseHashMap.put("success", true);
					responseHashMap.put("responseData", dataHashMap);


					
				} catch (Exception ex) {
					if (!dataHashMap.containsKey("errorData")) {
						logging.error("Provider : {} , Method : {} , ErrorMessage : {}", this.getClass().getName(),dpMethod, ex.getMessage());
						dataHashMap.put("errorData",new CustomErr(ErrConstants.invalidDataErrCode, ErrConstants.invalidDataErrMessage));
						responseHashMap.put("success", false);
						responseHashMap.put("responseData", dataHashMap);
					}
				}
			} 
			else {
				dataHashMap.put("errorData",new CustomErr(ErrConstants.methodNotFoundErrCode, ErrConstants.methodNotFoundErrMessage));
				responseHashMap.put("success", false);
				responseHashMap.put("responseData", dataHashMap);
			}
			return responseHashMap;
		}
		catch (Exception e) {
			if (!dataHashMap.containsKey("errorData")) {
				logging.error("Provider : {} , Method : {} , ErrorMessage : {}", this.getClass().getName(), dpMethod,e.getCause().getMessage());
				dataHashMap.put("errorData",new CustomErr(ErrConstants.invalidDataErrCode, ErrConstants.invalidDataErrMessage));
				responseHashMap.put("success", false);
				responseHashMap.put("responseData", dataHashMap);
			}

		}
		
		return responseHashMap;
	}
}
