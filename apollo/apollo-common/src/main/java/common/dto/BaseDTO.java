package common.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDTO {
    private String createAuthor;
    private LocalDateTime createTime;
    private String updateAuthor;
    private LocalDateTime updateTime;

}
