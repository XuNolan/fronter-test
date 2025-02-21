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
        return returnType.getContainingClass().isAnnotationPresent(AmisResult.class) || returnType.hasMethodAnnotation(AmisResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (Objects.nonNull(body) && Objects.nonNull(body.getClass())) {
            String simpleName = body.getClass().getSimpleName();
            if (RETURN_CLASS.equalsIgnoreCase(simpleName)) {
                return body;
            }
        }
        return BasicResultVO.success(body);
    }
}
