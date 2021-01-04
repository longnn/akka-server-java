package com.icod.ilearning.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "permission_group")
public class PermissionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    int id;

    @Column(name = "name")
    String name;

    @Column(name = "status")
    int status;
}
