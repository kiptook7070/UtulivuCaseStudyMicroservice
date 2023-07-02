package com.eclectics.System.segments.SubSegments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubSegmentService {
    @Autowired
    private SubSegmentRepo subSubSegmentRepo;

    public SubSegment addSubSegment(SubSegment subSubSegment) {
        try {
            return subSubSegmentRepo.save(subSubSegment);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public SubSegment updateSubSegment(SubSegment segment) {
        try {
            return subSubSegmentRepo.save(segment);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}

