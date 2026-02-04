package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断当前拦截到的是Controller的方法还是其他资源
        if(!(handler instanceof  HandlerMethod)){
            return true;
        }
        //从请求头中获取令牌
        String token = request.getHeader("authentication");
        if(token==null || token == ""){
            return false;
        }
        //校验令牌
        try{
            log.info("jwt校验:{}",token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(),token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前员工id:",userId);
            BaseContext.setCurrentId(userId);
            //通过
            return  true;

        }catch(Exception e){
            response.setStatus(401);
            return false;
        }
    }

}
