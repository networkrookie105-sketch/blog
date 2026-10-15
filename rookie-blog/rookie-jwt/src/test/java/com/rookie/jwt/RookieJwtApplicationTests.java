package com.rookie.jwt;

import com.rookie.jwt.utils.JwtTokenHelper;
import com.rookie.jwt.utils.TokenBlacklist;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class RookieJwtApplicationTests {
    @Test
    void signsAndVerifiesTokensOnJava21() throws Exception {
        JwtTokenHelper helper = new JwtTokenHelper();
        helper.setBase64Key(Base64.getEncoder().encodeToString(new byte[64]));
        ReflectionTestUtils.setField(helper, "issuer", "test");
        ReflectionTestUtils.setField(helper, "tokenExpireTime", 60L);
        helper.afterPropertiesSet();
        String token = helper.generateToken("admin");
        assertEquals("admin", helper.parseToken(token).getBody().getSubject());
        assertNotEquals(token, helper.generateToken("admin"), "重新登录不能复用已登出的 Token");
        assertThrows(BadCredentialsException.class, () -> helper.parseToken("invalid"));
    }

    @Test
    void initializesAndUsesCaffeineBlacklist() {
        TokenBlacklist blacklist = new TokenBlacklist();
        ReflectionTestUtils.setField(blacklist, "tokenExpireTime", 60L);
        blacklist.init();
        assertFalse(blacklist.contains("token"));
        blacklist.add("token");
        assertTrue(blacklist.contains("token"));
    }
}
