package com.yumu.hexie.model.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrgMenuRepository extends JpaRepository<OrgMenu, Long>{
    @Query(value = "select DISTINCT m.* from orgmenu m " +
            "join orgRoleMenu mr on mr.menuCode = m.code " +
            "where m.status = 1 and IF (?1!='', mr.roleId = ?1, 1=1) " +
            "and IF (?2!='', mr.orgType = ?2, 1=1) and m.menuLevel = ?3 " +
            "and IF (?4!='', m.parentCode = ?4, 1=1) order by m.sort ",
            nativeQuery = true)
    List<OrgMenu> findByUserRoleAndLevel(String roleId, String orgType, String menuLevel, String parentCode);

}
