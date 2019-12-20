package common.dto;


import lombok.Data;

import java.util.Date;

@Data
public class BaseDTO {
    private String createAuthor;
    private Date createTime;
    private String updateAuthor;
    private Date updateTime;

}
