package com.sai.lendperfect.common.security;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.sai.lendperfect.commodel.LpcomProposal;
import com.sai.lendperfect.commodel.LpcomSecurity;

public interface LpcomSecurityService {
	long findSecCount(long custId);
	List<LpcomSecurity> findAll();
	LpcomSecurity saveLpcomSecurity(LpcomSecurity lpcomSecurity);
	void deleteLpcomSecurity(LpcomSecurity lpcomSecurity);
	LpcomSecurity findByLsSecId(long lsSecId);
	
LpcomSecurity findByLsBgAson(Date lsBgAson);
	
	LpcomSecurity findByLsAddress(String lsAddress);
	
	List<LpcomSecurity> findByLsSecIdAndLsSecTypeAndLsSecClassification(long lsSecId,BigDecimal lsSecType,BigDecimal lsSecClassification);
	LpcomSecurity findByLsSecIdAndLsDelete(long lsSecId, String lsDelete);
	String getsecdenom (LpcomProposal lpcomProposal,BigDecimal secid,BigDecimal custid);
	List<Object> getSecList(LpcomProposal lpcomProposal,BigDecimal custid);
	String gettotsecnetval (LpcomProposal lpcomProposal);
}