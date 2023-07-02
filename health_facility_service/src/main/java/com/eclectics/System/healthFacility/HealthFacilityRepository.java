package com.eclectics.System.healthFacility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthFacilityRepository extends JpaRepository<HealthFacility, Long> {
    Optional<HealthFacility> findHealthFacilityByCode(String code);
    @Query(value = "SELECT `id` AS Id, `code` AS itemCode FROM health_facility ORDER BY `code` DESC LIMIT 1", nativeQuery = true)
    Optional<getItemsData> findLastEntry();
    public interface getItemsData {
        Long getId();
        String getItemCode();
    }
}
