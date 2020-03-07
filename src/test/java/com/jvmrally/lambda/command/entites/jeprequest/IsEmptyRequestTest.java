package com.jvmrally.lambda.command.entites.jeprequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * JepRequestTest
 */
public class IsEmptyRequestTest {
    private final JepRequestBuilder JEP_REQUEST_BUILDER = new JepRequestBuilder();

    @Test
    void testWithEmptyRequest() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER.build();
        assertTrue(subject.isEmptyRequest());
    }

    @Test
    void testFullRequest() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .jepId(8240497)
            .searchParam("switch-expressions")
            .statusName("DRAFT")
            .type("FEATURE")
            .release("14")
            .build();
        
        assertFalse(subject.isEmptyRequest());
    }

    @Test
    void testWithOnlyJepId() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .jepId(8240497)
            .build();
        
        assertFalse(subject.isEmptyRequest());
    }

    @Test
    void testWithOnlySearchParam() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .searchParam("switch-expressions")
            .build();
        
        assertFalse(subject.isEmptyRequest());
    }

    @Test
    void testWithOnlyStatusName() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .statusName("DRAFT")
            .build();
        
        assertFalse(subject.isEmptyRequest());
    }

    @Test
    void testWithOnlyType() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .type("FEATURE")
            .build();
        
        assertFalse(subject.isEmptyRequest());
    }

    @Test
    void testWithOnlyRelease() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .release("14")
            .build();
        
        assertFalse(subject.isEmptyRequest());
    }
        
}