package com.eclectics.System.clinicalOfficer;

import com.eclectics.System.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ClinicalOfficerService {
    private final ClinicalOfficerRepository clinicalOfficerRepository;

    public ClinicalOfficerService(ClinicalOfficerRepository clinicalOfficerRepository) {
        this.clinicalOfficerRepository = clinicalOfficerRepository;
    }

    public EntityResponse addHealthOfficer(ClinicalOfficer clinicalOfficer) {
        try {
            EntityResponse response = new EntityResponse();
                ClinicalOfficer addOfficer = clinicalOfficerRepository.save(clinicalOfficer);
                response.setMessage("Created Successfully");
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(addOfficer);

            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getItemsList() {
        try {
            EntityResponse response = new EntityResponse();


            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .get()
                    .url("http://localhost:9003/api/v1/health-facility/all")
                    .build();
            Response resp = client.newCall(request).execute();
            String res = resp.body().string();
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(res);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse update(ClinicalOfficer clinicalOfficer) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<ClinicalOfficer> clinicalOfficer1 = clinicalOfficerRepository.findById(clinicalOfficer.getId());
            if (clinicalOfficer1.isPresent()){
                ClinicalOfficer addData = clinicalOfficerRepository.save(clinicalOfficer);
                response.setMessage("Update Successfully");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(addData);
            } else {
                response.setMessage("Member Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }

            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse getAll() {
        try {
            EntityResponse response = new EntityResponse();
            List<ClinicalOfficer> clinicalOfficers = clinicalOfficerRepository.findAll();
            if (clinicalOfficers.size() > 0) {
                response.setMessage("Found");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(clinicalOfficers);
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
            Optional<ClinicalOfficer> clinicalOfficer = clinicalOfficerRepository.findById(id);
            if (clinicalOfficer.isPresent()) {
                clinicalOfficerRepository.deleteById(id);
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
