package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.AgentAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentActionRepository extends JpaRepository<AgentAction, Long> {

	List<AgentAction> findByConversationIdOrderByCreatedAtDesc(Long conversationId);

	List<AgentAction> findByStatusIn(List<String> statuses);
}
