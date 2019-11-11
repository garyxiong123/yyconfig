package com.yofish.gary.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@DiscriminatorValue("function")
public class Permission4Function extends Permission {


    @Column(name = "fucntion_url")
    private String fucntionUrl;


}
