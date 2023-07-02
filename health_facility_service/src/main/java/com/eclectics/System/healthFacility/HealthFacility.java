package com.eclectics.System.healthFacility;

import com.eclectics.System.clinicalOfficer.ClinicalOfficer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class HealthFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String location;
    private String address;
    private String hod;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "healthFacility")
//    @JsonIgnore
    private List<ClinicalOfficer> clinicalOfficers;
}
