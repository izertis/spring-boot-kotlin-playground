package com.izertis.example.service.dtos;

import java.io.Serializable;

/** CustomerSearchCriteria. */
@lombok.Getter
@lombok.Setter
public class CustomerSearchCriteria implements Serializable {

    private String name;

    private String email;

    private String city;

    private String state;

}
