package com.eclectics.System.clinicalOfficer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicalOfficerRepository extends JpaRepository<ClinicalOfficer, Long> {

}
