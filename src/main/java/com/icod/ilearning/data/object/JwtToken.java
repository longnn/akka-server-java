package com.icod.ilearning.data.object;

import com.icod.ilearning.data.model.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class JwtToken {
    String jwt;
    RefreshToken refreshToken;
    Date expireDate;
}
