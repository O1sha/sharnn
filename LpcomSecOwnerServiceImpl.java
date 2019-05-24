package com.sai.lendperfect.common.security;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sai.lendperfect.commodel.LpcomSecOwner;
import com.sai.lendperfect.commodel.LpcomSecurity;
import com.sai.lendperfect.comrepo.LpcomSecOwnerRepo;

@Service("LpcomSecOwnerService")
@Transactional
public class LpcomSecOwnerServiceImpl implements LpcomSecOwnerService{
	
	@Autowired
	LpcomSecOwnerRepo lpcomSecOwnerRepo;


	public void deleteLpcomSecOwner(LpcomSecOwner lpcomSecOwner) {
		lpcomSecOwnerRepo.delete(lpcomSecOwner);
	}

	public LpcomSecOwner findByLsoOwnerIdAndLsoOwnerSno(BigDecimal ownerId, BigDecimal lsoOwnerSno) {
		return lpcomSecOwnerRepo.findByLsoOwnerIdAndLsoOwnerSno(ownerId, lsoOwnerSno);
	}

	public List<LpcomSecOwner> saveLpcomSecOwner(List<LpcomSecOwner> lpcomSecOwnerList) {
		return lpcomSecOwnerRepo.save(lpcomSecOwnerList);
	}

	public List<LpcomSecOwner> findByLsoOwnerIdOrderByLsoRowId(BigDecimal ownerId) {
		return lpcomSecOwnerRepo.findByLsoOwnerIdOrderByLsoRowId(ownerId);
	}

	public List<LpcomSecOwner> findByLpcomSecurity(LpcomSecurity lpcomSecurity) {
		return lpcomSecOwnerRepo.findByLpcomSecurity(lpcomSecurity);
	}

	public LpcomSecOwner findByLsoRowId(BigDecimal lsoRowId) {
		return lpcomSecOwnerRepo.findByLsoRowId(lsoRowId);
	}

	@Override
	public List<Object> findSecurities(long propNo) {
		return lpcomSecOwnerRepo.findSecurities(propNo);
	}

	public void delete(List<LpcomSecOwner> lpcomSecOwner) {
		lpcomSecOwnerRepo.delete(lpcomSecOwner);
		
	}

}
