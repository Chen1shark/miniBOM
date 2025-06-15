package com.idme.interceptor;

import com.alibaba.fastjson.JSON;
import com.idme.constant.JwtClaimsConstant;
import com.idme.context.BaseContext;
import com.idme.exception.BaseException;
import com.idme.properties.JwtProperties;
import com.idme.utils.HttpClientUtil;
import com.idme.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {


    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2、校验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());

            // 构建华为云认证请求
            Map<String, Object> authMap = new HashMap<>();
            Map<String, Object> identityMap = new HashMap<>();
            Map<String, Object> passwordMap = new HashMap<>();
            Map<String, Object> userMap = new HashMap<>();
            Map<String, Object> domainMap1 = new HashMap<>();
            Map<String, Object> domainMap2 = new HashMap<>();
            Map<String, Object> auth = new HashMap<>();

            domainMap1.put("name", "hid_6tw258mfirqdmma");
            domainMap2.put("name", "hid_6tw258mfirqdmma");
            userMap.put("domain", domainMap1);
            userMap.put("name", "IAMUserQRD");
            userMap.put("password", "20040428Qrd");
            passwordMap.put("user", userMap);
            identityMap.put("methods", Arrays.asList("password"));
            identityMap.put("password", passwordMap);
            authMap.put("identity", identityMap);

            Map<String, Object> scopeMap = new HashMap<>();
            scopeMap.put("domain", domainMap2);
            authMap.put("scope", scopeMap);
            auth.put("auth", authMap);

            String tokenUrl = "https://iam.myhuaweicloud.com/v3/auth/tokens?nocatalog=true";

            try {
                // 使用工具类发送请求
                HttpClientUtil.HttpResponse httpResponse = HttpClientUtil.doPost4Json(tokenUrl, auth);

                // 获取X-Subject-Token
                String xSubjectToken = httpResponse.getHeader("X-Subject-Token");
                if (xSubjectToken == null) {
                    throw new RuntimeException("华为云认证失败：未获取到X-Subject-Token");
                }
                BaseContext.setCurrentToken(xSubjectToken);
                //3、通过，放行
                return true;
            } catch (Exception ex) {
                log.error("华为云认证失败", ex);
                response.setStatus(401);
                return false;
            }

        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
