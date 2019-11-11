package com.yofish.gary.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@DiscriminatorValue("data")
public class Permission4Data extends Permission {

    private String tableName;

    private String columnName;


}
