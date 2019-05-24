package com.sai.lendperfect.common.security;


import java.math.BigDecimal;
import java.util.List;

import com.sai.lendperfect.commodel.LpcomSecOwner;
import com.sai.lendperfect.commodel.LpcomSecurity;

public interface LpcomSecOwnerService {
	
	List<LpcomSecOwner> saveLpcomSecOwner(List<LpcomSecOwner> lpcomSecOwnerList);
	void deleteLpcomSecOwner(LpcomSecOwner lpcomSecOwner);
	LpcomSecOwner findByLsoOwnerIdAndLsoOwnerSno(BigDecimal OwnerId, BigDecimal lsoOwnerSno);
	List<LpcomSecOwner> findByLsoOwnerIdOrderByLsoRowId(BigDecimal ownerId);
	List<LpcomSecOwner> findByLpcomSecurity(LpcomSecurity lpcomSecurity);
	LpcomSecOwner findByLsoRowId(BigDecimal lsoRowId);
	List<Object> findSecurities(long propNo);
	void delete(List<LpcomSecOwner> lpcomSecOwner);
}
