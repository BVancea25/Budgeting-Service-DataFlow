package com.dataflow.budgetingservice.Errors;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/budgets");

    @Test
    void dataIntegrityViolationReturnsSanitizedConflict() {
        var response = handler.handleConflict(
                new DataIntegrityViolationException("Duplicate entry for table budgets column category_id"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ApiErrorCode.DATA_CONFLICT);
        assertThat(response.getBody().message()).doesNotContain("budgets", "category_id", "Duplicate entry");
    }

    @Test
    void forbiddenOperationReturnsSanitizedForbidden() {
        var response = handler.handleForbidden(new ForbiddenOperationException("budget 123 owned by another user"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ApiErrorCode.FORBIDDEN);
        assertThat(response.getBody().message()).doesNotContain("123");
    }
}
