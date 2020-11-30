package com.icod.ilearning.data.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.icod.ilearning.services.protocol.user.UserModelSerializer;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
@JsonIgnoreProperties({"password"})
@JsonPropertyOrder({"id","name","email","createdAt","updatedAt"})
@JsonSerialize(using = UserModelSerializer.class)
public class UserModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JsonProperty("name")
    @Column(name = "name")
    String name;

    @JsonProperty("email")
    @Column(name = "email")
    String email;

    @JsonProperty("password")
    @Column(name = "password")
    String password;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "created_at")
    Date createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "updated_at")
    Date updatedAt;

    @JsonProperty("status")
    @Column(name = "status")
    int status;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    Set<RoleModel> roles = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_permission", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    Set<PermissionModel> permissions = new HashSet<>();
}
