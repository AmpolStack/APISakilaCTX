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

@Entity
@Getter
@Setter
public class Address implements IVerifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int id;
    @Column(nullable = false)
    private String address;
    private String address2;
    @Column(nullable = false)
    private String district;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    private String postal_code;
    private String phone;
    private Date last_update;

    @Override
    public Result<Void> Verify() {
        var errors = new ArrayList<String>();

        if(this.id < 0  || this.id > 255){
            errors.add("Address ID must be between 0 and 255");
        }

        if(this.address.isBlank() || this.address.length() > 50){
            errors.add("Address is blank and max length is 50 characters");
        }

        if(!this.address2.isBlank() && this.address2.length() > 50){
            errors.add("Address2 max length is 50 characters");
        }

        if(this.district.isBlank() || this.district.length() > 20){
            errors.add("District is blank and max length is 20 characters");
        }

        if(!this.postal_code.isBlank() && this.postal_code.length() > 10){
            errors.add("PostalCode max length is 10 characters");
        }

        if(this.phone.isBlank() || this.phone.length() > 20){
            errors.add("Phone is blank and max length is 20 characters");
        }

        if(this.last_update == null){
            errors.add("Last update is null");
        }

        if(errors.isEmpty()){
            return Result.Success();
        }

        return Result.Failed(new Error(errors, ErrorType.VALIDATION_ERROR));

    }
}
