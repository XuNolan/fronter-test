package project.xunolan.web.amisEntity.aspect;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import project.xunolan.common.entity.BasicResultVO;

import java.util.Objects;

@ControllerAdvice(basePackages = "project.xunolan.web.controller")
public class AmisResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final String RETURN_CLASS = "BasicResultVO";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 检查是否有 @AmisResult 注解
        return returnType.getContainingClass().isAnnotationPresent(AmisResult.class) || returnType.hasMethodAnnotation(AmisResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (Objects.nonNull(body) && Objects.nonNull(body.getClass())) {
            // 如果已经是 BasicResultVO，直接返回
            String simpleName = body.getClass().getSimpleName();
            if (RETURN_CLASS.equalsIgnoreCase(simpleName)) {
                return body;
            }
        }
        return BasicResultVO.success(body);
    }
    //转换效果为，将body包装为data成员内容，返回如下：
    //{
    //  "status": 0,
    //  "msg": "",
    //  "data": scriptList
    //}
}
