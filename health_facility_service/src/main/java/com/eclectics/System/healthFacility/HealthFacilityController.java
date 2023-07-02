package com.eclectics.System.healthFacility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/health-facility")
public class HealthFacilityController {
    private final HealthFacilityService healthFacilityService;

    public HealthFacilityController(HealthFacilityService healthFacilityService) {
        this.healthFacilityService = healthFacilityService;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addHealthFacility(@RequestBody HealthFacility healthFacility) {
        try {
            return ResponseEntity.ok().body(healthFacilityService.addHealthFacility(healthFacility));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getItems() {
        try {
            return ResponseEntity.ok().body(healthFacilityService.getItems());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(HealthFacility healthFacility) {
        try {
            return ResponseEntity.ok().body(healthFacilityService.update(healthFacility));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(healthFacilityService.delete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
