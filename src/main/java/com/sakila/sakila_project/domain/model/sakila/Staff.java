package com.sakila.sakila_project.domain.model.sakila;

import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.IVerifiable;
import com.sakila.sakila_project.domain.results.Result;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
public class Staff implements IVerifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private int id;
    private String first_name;
    private String last_name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "address_id")
    private Address address;
    private byte[] picture;
    @Column(length = 50)
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
    private List<Payment> payments;
    private int active;
    private String username;
    private String password;
    private Date last_update;

    @Override
    public Result<Void> Verify() {

        var errors = new ArrayList<String>();

        if(this.id < 0 || this.id > 255){
            errors.add("The id are out of range or not exist");
        }

        if(IsBlankOrNull(this.first_name) || this.first_name.length() > 45){
            errors.add("The first name is required and ≤ 45 characters");
        }

        if(IsBlankOrNull(this.last_name) || this.last_name.length() > 45){
            errors.add("The last name is required and ≤ 45 characters");
        }

        final int MAX_BLOB_BYTES = 16 * 1024 * 1024;
        if(this.picture != null && this.picture.length > MAX_BLOB_BYTES){
            errors.add("The picture exceeds the maximum allowed size (16MB)");
        }

        if(this.email != null){
            var emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
                            Pattern.CASE_INSENSITIVE);

            if(this.email.length() > 50){
                errors.add("The email address exceeds the maximum allowed size (50 characters)");
            }
            if (!emailPattern.matcher(this.email).matches()) {
                errors.add("The email sent is invalid");
            }

        }

        if(IsBlankOrNull(this.username) || this.username.length() > 16){
            errors.add("The username is required and the max length is 16 characters");
        }

        if(this.password != null && this.password.length() > 40){
            errors.add("The password is required and the max length is 40 characters");
        }

        if(this.last_update == null){
            errors.add("The last update cannot be null");
        }

        if(!errors.isEmpty()){
            return Result.Failed(new Error(errors, ErrorType.VALIDATION_ERROR));
        }

        return Result.Success();
    }

    private static boolean IsBlankOrNull(String str){
        return str == null || str.trim().isEmpty();
    }

}

