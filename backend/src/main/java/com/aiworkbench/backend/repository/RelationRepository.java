package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<Relation, Long> {

	List<Relation> findByFromTypeAndFromId(String fromType, Long fromId);

	List<Relation> findByToTypeAndToId(String toType, Long toId);
}
