package com.eclectics.System.healthFacility;

import com.eclectics.System.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HealthFacilityService {
    private final HealthFacilityRepository healthFacilityRepository;

    public HealthFacilityService(HealthFacilityRepository healthFacilityRepository) {
        this.healthFacilityRepository = healthFacilityRepository;
    }

    public EntityResponse addHealthFacility(HealthFacility healthFacility) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<HealthFacility> searchCode = healthFacilityRepository.findHealthFacilityByCode(healthFacility.getCode());
            if (searchCode.isPresent()) {
                response.setMessage("Health with code " + healthFacility.getCode() + " Already Registered: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                String prefixCharacters = "HTF";
                String remainingFourDigits = "";
                Optional<HealthFacilityRepository.getItemsData> itemsData = healthFacilityRepository.findLastEntry();
                if (itemsData.isPresent()) {
                    String itemCode = itemsData.get().getItemCode();
                    String lastFourCharacters = itemCode.substring(itemCode.length() - 4);
                    Long lastFourDigits = Long.valueOf(lastFourCharacters);
                    String newCode = String.valueOf((lastFourDigits + 1));
                    do {
                        newCode = "0" + newCode;
                    } while (newCode.length() < 4);

                    remainingFourDigits = newCode;
                } else {
                    remainingFourDigits = "0001";
                }
                String generatedItemCode = prefixCharacters + remainingFourDigits;
                healthFacility.setCode(generatedItemCode);


                HealthFacility addItem = healthFacilityRepository.save(healthFacility);
                response.setMessage("Health Facility With Code " + addItem.getCode() + " Created Successfully");
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(addItem);
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getItems() {
        try {
            EntityResponse response = new EntityResponse();
            List<HealthFacility> itemsList = healthFacilityRepository.findAll();
            if (itemsList.size() > 0) {
                response.setMessage(itemsList.size() + " Already Created");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(itemsList);
            } else {
                response.setMessage("NO Single Item Added: Please Create Some items to consume this API URL !!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse update(HealthFacility healthFacility) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<HealthFacility> healthFacility1 = healthFacilityRepository.findById(healthFacility.getId());
            if (healthFacility1.isPresent()) {
                HealthFacility addData = healthFacilityRepository.save(healthFacility);
                response.setMessage("Update Successfully");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(addData);
            } else {
                response.setMessage("Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }

            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse delete(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<HealthFacility> healthFacility1 = healthFacilityRepository.findById(id);
            if (healthFacility1.isPresent()) {
                healthFacilityRepository.deleteById(id);
                response.setMessage("Deleted Successfully");
                response.setStatusCode(HttpStatus.OK.value());
            } else {
                response.setMessage("Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
