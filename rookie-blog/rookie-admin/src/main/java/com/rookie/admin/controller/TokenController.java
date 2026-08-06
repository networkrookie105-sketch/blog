package com.rookie.admin.controller;

import com.rookie.common.utils.Response;
import com.rookie.jwt.utils.JwtTokenHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peishunwu
 * @ClassName TokenController
 * @description: TODO
 * @date 2024年04月28日
 * @version: 1.0
 */
@RestController
@RequestMapping("/token")
@Api(tags = "获取token")
public class TokenController {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @GetMapping ("/info")
    public Response getToken(String username) {
        //生成Token
        String token = jwtTokenHelper.generateToken(username);
        return Response.success("Bearer "+token);
    }
}
