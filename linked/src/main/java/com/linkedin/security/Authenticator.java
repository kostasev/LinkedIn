package com.linkedin.security;

import com.linkedin.controller.UserController;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;


public class Authenticator {

    private String token;;
    private String type;
    private int userid;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Authenticator(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public Authenticator(String token) {
        this.token = token;
    }


    public void authenticate(String token) throws Exception {
        try {
            Claims claims = Jwts.parser().setSigningKey(UserController.key).parseClaimsJws(this.token).getBody();
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            if (now.after(claims.getExpiration()))
                throw new Exception();
            this.type = (String) claims.get("role");
            this.userid = (Integer) claims.get("id");
        } catch (Exception e) {
            throw e;
        }
    }
}
