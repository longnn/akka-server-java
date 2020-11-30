package com.icod.ilearning.data.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "refresh_token")
public class RefreshTokenModel {
    @Id
    @Column(name = "id")
    long id;

    @Column(name = "refresh_token")
    String token;

    @Column(name = "user_id")
    long userId;

    @Column(name = "created_at")
    Date createdAt;

    @Column(name = "expired_at")
    Date expiredAt;
}
