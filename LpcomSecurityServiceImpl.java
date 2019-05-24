package com.sai.lendperfect.common.security;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sai.lendperfect.commodel.LpcomProposal;
import com.sai.lendperfect.commodel.LpcomSecurity;
import com.sai.lendperfect.comrepo.LpcomSecurityRepo;

@Service("lpcomSecurityService")
@Transactional

public class LpcomSecurityServiceImpl implements LpcomSecurityService  {
	
	@Autowired
	private LpcomSecurityRepo  lpcomSecurityRepo;

	public List<LpcomSecurity> findAll() {
		return lpcomSecurityRepo.findAll();
	}

	public LpcomSecurity saveLpcomSecurity(LpcomSecurity lpcomSecurity) {
		return lpcomSecurityRepo.save(lpcomSecurity);
	}

	public void deleteLpcomSecurity(LpcomSecurity lpcomSecurity) {
		lpcomSecurityRepo.delete(lpcomSecurity);
	}

	public LpcomSecurity findByLsSecId(long lsSecId) {
		return lpcomSecurityRepo.findOne(lsSecId);
	}

	@Override
	public List<LpcomSecurity> findByLsSecIdAndLsSecTypeAndLsSecClassification(long lsSecId, BigDecimal lsSecType,
			BigDecimal lsSecClassification) {
		// TODO Auto-generated method stub
		return lpcomSecurityRepo.findByLsSecIdAndLsSecTypeAndLsSecClassification(lsSecId, lsSecType, lsSecClassification);
	}

	public long findSecCount(long custId) {
		return lpcomSecurityRepo.findSecCount(custId);
	}

	public LpcomSecurity findByLsSecIdAndLsDelete(long lsSecId, String lsDelete) {
		return lpcomSecurityRepo.findByLsSecIdAndLsDelete(lsSecId, lsDelete);
	}

	@Override
	public String getsecdenom(LpcomProposal lpcomProposal, BigDecimal secid,BigDecimal custid) {
		// TODO Auto-generated method stub
		return lpcomSecurityRepo.getsecdenom(lpcomProposal, secid,custid);
	}
	@Override
	public List<Object> getSecList(LpcomProposal lpcomProposal, BigDecimal custid) {
		return lpcomSecurityRepo.getSecList(lpcomProposal, custid);
	}
	@Override
	public String gettotsecnetval(LpcomProposal lpcomProposal) {
		// TODO Auto-generated method stub
		return lpcomSecurityRepo.gettotsecnetval(lpcomProposal);
	}

	@Override
	public LpcomSecurity findByLsBgAson(Date lsBgAson) {
		// TODO Auto-generated method stub
		return lpcomSecurityRepo.findByLsBgAson(lsBgAson);
	}

	@Override
	public LpcomSecurity findByLsAddress(String lsAddress) {
		// TODO Auto-generated method stub
		return lpcomSecurityRepo.findByLsAddress(lsAddress);
	}
}
