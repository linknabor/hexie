package com.yumu.hexie.model.agent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgentRepository extends JpaRepository<Agent, Long>{

	@Query(value = "select a.id from agent a where a.status =?1 "
			+ "and IF (?2!='', a.agentNo = ?2, 1=1 )"
			+ "and IF (?3!='', a.name like CONCAT('%',?3,'%'), 1=1) ", nativeQuery = true)
	List<Integer> findByAgentNoOrName(int status, String agentNo, String agentName);
	
	Agent findByAgentNo(String agentNo);
}
