package com.rookie.admin;

import com.rookie.admin.model.vo.article.PublishArticleReqVO;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RookieAdminApplicationTests {
    @Test
    void validatesArticleRequestsWithJakarta() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator().validate(new PublishArticleReqVO());
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("categoryId")));
        }
    }
}
