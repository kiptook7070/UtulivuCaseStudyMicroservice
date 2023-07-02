package com.eclectics.System.segments.SubSegments;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubSegmentRepo extends JpaRepository<SubSegment, Long> {
    Optional<SubSegment> findSubSegmentByEntityIdAndId(String entityId, Long id);
    Optional<SubSegment> findByEntityIdAndSubSegmentCodeAndDeletedFlag(String entityId, String subSegmentCode, Character flag);
    List<SubSegment> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<SubSegment> findByEntityIdAndSubSegmentCode(String entityId, String subSegmentCode);
    List<SubSegment> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}