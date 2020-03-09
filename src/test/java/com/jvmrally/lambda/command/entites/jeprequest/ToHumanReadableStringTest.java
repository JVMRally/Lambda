package com.jvmrally.lambda.command.entites.jeprequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        
        assertFalse(subject.contains("id"));   
        assertFalse(subject.contains("'8240497'"));    
        assertFalse(subject.contains("'exception'"));
        assertFalse(subject.contains("term"));
        assertFalse(subject.contains("'DRAFT'"));
        assertFalse(subject.contains("status"));
        assertFalse(subject.contains("'FEATURE'"));
        assertFalse(subject.contains("type"));
        assertFalse(subject.contains("'14'"));
        assertFalse(subject.contains("release"));  

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
   
        assertFalse(subject.contains("'exception'"));
        assertFalse(subject.contains("term"));
        assertFalse(subject.contains("'DRAFT'"));
        assertFalse(subject.contains("status:"));
        assertFalse(subject.contains("'FEATURE'"));
        assertFalse(subject.contains("type"));
        assertFalse(subject.contains("'14'"));
        assertFalse(subject.contains("release"));  
    }

    @Test
    void testWithOnlySearchParam() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .searchParam("exception")
            .build()
            .toHumanReadableString();

        assertTrue(subject.contains("term: 'exception'"));

        assertFalse(subject.contains("id"));   
        assertFalse(subject.contains("'8240497'"));     
        assertFalse(subject.contains("'DRAFT'"));
        assertFalse(subject.contains("status:"));
        assertFalse(subject.contains("'FEATURE'"));
        assertFalse(subject.contains("type"));
        assertFalse(subject.contains("'14'"));
        assertFalse(subject.contains("release"));  
    }

    @Test
    void testWithOnlyStatusName() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .statusName("draft")
            .build()
            .toHumanReadableString();
            
        assertTrue(subject.contains("status: 'DRAFT'"));

        assertFalse(subject.contains("id"));   
        assertFalse(subject.contains("'8240497'"));     
        assertFalse(subject.contains("'exception'"));
        assertFalse(subject.contains("term"));
        assertFalse(subject.contains("'FEATURE'"));
        assertFalse(subject.contains("type"));
        assertFalse(subject.contains("'14'"));
        assertFalse(subject.contains("release"));  
    }

    @Test
    void testWithOnlyType() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .type("feature")
            .build()
            .toHumanReadableString();
            
        assertTrue(subject.contains("type: 'FEATURE'"));

        assertFalse(subject.contains("id"));   
        assertFalse(subject.contains("'8240497'"));     
        assertFalse(subject.contains("'exception'"));
        assertFalse(subject.contains("term"));
        assertFalse(subject.contains("'DRAFT'"));
        assertFalse(subject.contains("status:"));
        assertFalse(subject.contains("'14'"));
        assertFalse(subject.contains("release"));  
    }

    @Test
    void testWithOnlyRelease() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .release("14")
            .build()
            .toHumanReadableString();
            
        assertTrue(subject.contains("release: '14'"));

        assertFalse(subject.contains("id"));   
        assertFalse(subject.contains("'8240497'"));     
        assertFalse(subject.contains("'exception'"));
        assertFalse(subject.contains("term"));
        assertFalse(subject.contains("'DRAFT'"));
        assertFalse(subject.contains("status:"));
        assertFalse(subject.contains("'FEATURE'"));
        assertFalse(subject.contains("type"));
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
   
        assertFalse(subject.contains("'exception'"));
        assertFalse(subject.contains("term"));
        assertFalse(subject.contains("'FEATURE'"));
        assertFalse(subject.contains("type"));
    }

    @Test
    void testCorrectCommaplacement() throws ReflectiveOperationException {
        var subject = JEP_REQUEST_BUILDER
            .jepId(8240497)
            .statusName("draft")
            .release("14")
            .build()
            .toHumanReadableString();

        assertFalse(subject.trim().startsWith(","));
        assertEquals(2, countCommas(subject));

        subject = JEP_REQUEST_BUILDER
            .release("14")
            .build()
            .toHumanReadableString();

        
        assertFalse(subject.trim().startsWith(","));
        assertEquals(0, countCommas(subject));
    }

    private int countCommas(String input) {
        int count = 0;
        for(var c : input.toCharArray()) {
            if(c == ',') {
                count++;
            }
        }
        return count;
    }
    
}