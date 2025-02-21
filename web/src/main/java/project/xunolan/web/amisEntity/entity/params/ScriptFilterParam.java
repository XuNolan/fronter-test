package project.xunolan.web.amisEntity.entity.params;

import com.sun.istack.NotNull;
import lombok.*;

/*
   用例详情页下的脚本crud参数
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScriptFilterParam {

    @NotNull
    private Integer page = 1;

    @NotNull
    private Integer perPage = 10;

    private String keyword;

    @NotNull
    private Long usecaseId;
}
