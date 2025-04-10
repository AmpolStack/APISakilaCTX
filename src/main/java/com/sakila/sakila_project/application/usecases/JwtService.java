package com.sakila.sakila_project.application.usecases;

import com.sakila.sakila_project.application.custom.AuthenticationRequest;
import com.sakila.sakila_project.application.custom.AuthenticationResponse;
import com.sakila.sakila_project.application.custom.Credentials;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.model.tokens.TokenRegistration;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.StaffRepository;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.tokens.TokenRegistrationRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService {

    @Value("${spring.security.jwt.token.expirationMs}")
    private int tokenExpiration;

    @Value("${spring.security.jwt.refresh.token.expirationMs}")
    private int refreshTokenExpiration;

    @Value("${spring.security.jwt.secret}")
    private String secret;

    private TokenRegistrationRepository tokenRepository;
    private StaffRepository staffRepository;

    @Autowired
    public JwtService(TokenRegistrationRepository tokenRepository, StaffRepository staffRepository) {
        this.tokenRepository = tokenRepository;
        this.staffRepository = staffRepository;
    }

    private SecretKey getSecretKey() {
        var keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public AuthenticationResponse AuthenticateByRefreshToken(AuthenticationRequest authenticationRequest) {
        var authResponse = new AuthenticationResponse();
        var tokenRegistryOp = this.tokenRepository.findTokenRegistrationByTokenAndRefreshToken(
                authenticationRequest.getToken(),
                authenticationRequest.getRefreshToken()
        );

        if(tokenRegistryOp.isEmpty()){
           authResponse.setMessage("Token not found");
           authResponse.setSuccess(false);
           return authResponse;
        }

        var tokenRegistry = tokenRegistryOp.get();
        if(tokenRegistry.getExpirationDate().before(new Date())){
            authResponse.setMessage("Token is expired");
            authResponse.setSuccess(false);
            return authResponse;
        }

        var staffOp = this.staffRepository.findById(tokenRegistry.getIdUser());
        if(staffOp.isEmpty()){
            authResponse.setMessage("Identifier not found");
            authResponse.setSuccess(false);
        }

        SaveRegistration(staffOp.get(), authResponse);
        return authResponse;

    }

    public AuthenticationResponse AuthenticateByCredentials(Credentials credentials) {
        var authResponse = new AuthenticationResponse();

        if(credentials.getUsername().isEmpty() || credentials.getPassword().isEmpty()) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Username and Password are required");
            return authResponse;
        }

        var staffOp = this.staffRepository.findStaffByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());

        if (staffOp.isEmpty()) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Invalid credentials");
            return authResponse;
        }

        var staff = staffOp.get();

        SaveRegistration(staff, authResponse);

        return authResponse;
    }

    private void SaveRegistration(Staff staff, AuthenticationResponse authResponse) {

        var claimMap = generateClaimsMap(staff);
        var token = generateToken(claimMap, staff.getUsername());
        var refreshToken = generateRefreshToken();

        var tokenRegistry = new TokenRegistration();
        tokenRegistry.setToken(token);
        tokenRegistry.setRefreshToken(refreshToken);
        tokenRegistry.setIdUser(staff.getId());
        tokenRegistry.setActive(true);
        tokenRegistry.setCreationDate(new Date());
        tokenRegistry.setExpirationDate(new Date(new Date().getTime() + refreshTokenExpiration));

        try{
            this.tokenRepository.save(tokenRegistry);
            authResponse.setSuccess(true);
            authResponse.setMessage("Authentication successful");
            authResponse.setToken(token);
            authResponse.setRefreshToken(refreshToken);
        }
        catch(Exception e){
            e.printStackTrace();
            authResponse.setSuccess(false);
            authResponse.setMessage("Error saving token, please try again later");
        }

    }

    private static Map<String, String> generateClaimsMap(Staff staff){
        var map = new HashMap<String, String>();
        map.put(Claims.ID, Integer.toString(staff.getId()));
        map.put("email", staff.getEmail());
        map.put("phone", staff.getAddress().getPhone());
        return map;
    }

    private String generateToken(Map<String, String> claims, String subject) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + this.tokenExpiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //128 Bits length
    private String generateRefreshToken(){
        return UUID.randomUUID().toString();
    }

    public boolean isTokenValid(String token) {
        var claims = getClaimsFromToken(token);
        return claims.getExpiration().after(new Date());
    }

    public List<Object> getClaimsList(String token, Map<String, Class<?>> map){
        var claims = getClaimsFromToken(token);
        var resultList = new ArrayList<>();

        map.forEach((k, v) ->{
            var valueClaim = claims.get(k);
            if(v.isInstance(valueClaim)){
                resultList.add(v);
            }
            else{
                throw new ClassCastException("Invalid claim");
                //resultList.add(null);
            }
        });
        return resultList;
    }


    public <T> T getClaim(String token, String claimName, Class<T> claimType){
        var claims = getClaimsFromToken(token);
        var claimValue = claims.get(claimName);
        if(claimType.isInstance(claimType)){
            return claimType.cast(claimValue);
        }
        else{
            throw new ClassCastException("Invalid claim");
            //return null;
        }
    }

    public Claims getAllClaims(String token){
        var claims = getClaimsFromToken(token);
        return claims;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
