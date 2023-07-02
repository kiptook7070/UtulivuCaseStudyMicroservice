package com.eclectics.System.clinicalOfficer;

import com.eclectics.System.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/vi/clinical-officer")

public class ClinicalOfficerController {
    private final ClinicalOfficerService clinicalOfficerService;

    public ClinicalOfficerController(ClinicalOfficerService clinicalOfficerService) {
        this.clinicalOfficerService = clinicalOfficerService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addHealthOfficer(@RequestBody ClinicalOfficer clinicalOfficer) {
        try {
            return ResponseEntity.ok().body(clinicalOfficerService.addHealthOfficer(clinicalOfficer));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok().body(clinicalOfficerService.getAll());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("/list")
    public ResponseEntity<?> getItemsList() {
        try {
            return ResponseEntity.ok().body(clinicalOfficerService.getItemsList());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(ClinicalOfficer clinicalOfficer) {
        try {
            return ResponseEntity.ok().body(clinicalOfficerService.update(clinicalOfficer));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(clinicalOfficerService.delete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

}
