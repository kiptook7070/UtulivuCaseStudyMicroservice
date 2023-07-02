package com.eclectics.System.segments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class Segmentservice {
    @Autowired
    private SegmentRepo segmentRepo;

    public Segment addSegment(Segment segment) {
        try {
            return segmentRepo.save(segment);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Segment> findAllSegments() {
        try {
            return segmentRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Segment updateSegment(Segment segment) {
        try {
            return segmentRepo.save(segment);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}

