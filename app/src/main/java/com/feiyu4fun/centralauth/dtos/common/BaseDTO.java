package com.feiyu4fun.centralauth.dtos.common;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@MappedSuperclass
@Data
public class BaseDTO {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    protected Long id;

}

