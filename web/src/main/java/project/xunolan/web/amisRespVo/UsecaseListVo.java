package project.xunolan.web.amisRespVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsecaseListVo {
    private List<Map<String, Object>> items;
    private int total;
    private int page;
}
