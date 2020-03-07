package com.jvmrally.lambda.command.entites.jeprequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * ToHumanReadableStringTest
 */
public class ToHumanReadableStringTest {

    private final JepRequestBuilder JEP_REQUEST_BUILDER = new JepRequestBuilder();

    @Test
    void testWithEmptyRequest() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER.build().toHumanReadableString();
        
        assertFalse(subject.contains("id: '8240497'"));
        assertFalse(subject.contains("term: 'exception'"));
        assertFalse(subject.contains("status: 'DRAFT'"));
        assertFalse(subject.contains("type: 'FEATURE'"));
        assertFalse(subject.contains("release: '14'"));
    }

    @Test
    void testFullRequest() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .jepId(8240497)
            .searchParam("exception")
            .statusName("draft")
            .type("feature")
            .release("14")
            .build()
            .toHumanReadableString();
        
        assertTrue(subject.contains("id: '8240497'"));
        assertTrue(subject.contains("term: 'exception'"));
        assertTrue(subject.contains("status: 'DRAFT'"));
        assertTrue(subject.contains("type: 'FEATURE'"));
        assertTrue(subject.contains("release: '14'"));
    }

    @Test
    void testWithOnlyJepId() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .jepId(8240497)
            .build()
            .toHumanReadableString();

        assertTrue(subject.contains("id: '8240497'"));

        assertFalse(subject.contains("term: 'exception'"));
        assertFalse(subject.contains("status: 'DRAFT'"));
        assertFalse(subject.contains("type: 'FEATURE'"));
        assertFalse(subject.contains("release: '14'"));
    }

    @Test
    void testWithOnlySearchParam() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .searchParam("exception")
            .build()
            .toHumanReadableString();

        assertTrue(subject.contains("term: 'exception'"));

        assertFalse(subject.contains("id: '8240497'"));
        assertFalse(subject.contains("status: 'DRAFT'"));
        assertFalse(subject.contains("type: 'FEATURE'"));
        assertFalse(subject.contains("release: '14'"));
    }

    @Test
    void testWithOnlyStatusName() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .statusName("draft")
            .build()
            .toHumanReadableString();
            
        assertTrue(subject.contains("status: 'DRAFT'"));

        assertFalse(subject.contains("id: '8240497'"));
        assertFalse(subject.contains("term: 'exception'"));
        assertFalse(subject.contains("type: 'FEATURE'"));
        assertFalse(subject.contains("release: '14'"));
    }

    @Test
    void testWithOnlyType() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .type("feature")
            .build()
            .toHumanReadableString();
            
        assertTrue(subject.contains("type: 'FEATURE'"));

        assertFalse(subject.contains("id: '8240497'"));
        assertFalse(subject.contains("term: 'exception'"));
        assertFalse(subject.contains("status: 'DRAFT'"));
        assertFalse(subject.contains("release: '14'"));
    }

    @Test
    void testWithOnlyRelease() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .release("14")
            .build()
            .toHumanReadableString();
            
        assertTrue(subject.contains("release: '14'"));

        assertFalse(subject.contains("id: '8240497'"));
        assertFalse(subject.contains("term: 'exception'"));
        assertFalse(subject.contains("status: 'DRAFT'"));
        assertFalse(subject.contains("type: 'FEATURE'"));
    }

    @Test
    void testWithSomeFields() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
        .jepId(8240497)
        .statusName("draft")
        .release("14")
        .build()
        .toHumanReadableString();
    
        assertTrue(subject.contains("id: '8240497'"));
        assertTrue(subject.contains("status: 'DRAFT'"));
        assertTrue(subject.contains("release: '14'"));

        assertFalse(subject.contains("term: 'exception'"));
        assertFalse(subject.contains("type: 'FEATURE'"));
    }
    
}