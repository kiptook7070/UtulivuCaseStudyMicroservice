package com.eclectics.usersservice.Roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    @JsonIgnore
    private String entityId;
    @Column(length = 20, nullable = false)
    private String roleCode;
    private Boolean managerial = false;
    @Column(length = 20, nullable = false)
    private String name;

    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
//    @JsonIgnore
    private String postedBy;
    @Column(nullable = false)
//    @JsonIgnore
    private Character postedFlag = 'Y';
//    @Column(nullable = false)
//    @JsonIgnore
    private Date postedTime;
//    @JsonIgnore
    private String modifiedBy;
//    @JsonIgnore
    private Character modifiedFlag = 'N';
//    @JsonIgnore
    private Date modifiedTime;
//    @JsonIgnore
    private String verifiedBy;
//    @JsonIgnore
    private Character verifiedFlag = 'N';
//    @JsonIgnore
    private Date verifiedTime;
//    @JsonIgnore
    private String deletedBy;
//    @JsonIgnore
    private Character deletedFlag = 'N';
//    @JsonIgnore
    private Date deletedTime;
}