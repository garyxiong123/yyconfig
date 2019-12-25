package common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO extends BaseDTO {

    private Long id;

    private Long namespaceId;

    private String key;

    private String value;

    private String comment;

    private Integer lineNum;
}
