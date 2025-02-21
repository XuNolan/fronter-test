package project.xunolan.web.amisEntity.entity.params;

import com.sun.istack.NotNull;
import lombok.*;

/*
   用例首页的脚本crud参数
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UsecaseFilterParam {

    @NotNull
    private Integer page = 1;

    @NotNull
    private Integer perPage = 10;

    private String keywords;

}