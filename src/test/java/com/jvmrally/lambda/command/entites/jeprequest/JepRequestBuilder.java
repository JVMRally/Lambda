package com.jvmrally.lambda.command.entites.jeprequest;

import com.jvmrally.lambda.command.entites.JepRequest;

/**
 * JepRequestFactory
 */
public class JepRequestBuilder {
    private final Integer JEP_ID;
    private final String SEARCH_PARAM;
    private final String STATUS_NAME;
    private final String TYPE;
    private final String RELEASE;

    public JepRequestBuilder() {
        JEP_ID = -1;
        SEARCH_PARAM = "";
        STATUS_NAME = "";
        TYPE = "";
        RELEASE = "";
    }

    private JepRequestBuilder(Integer jepId, String searchParam, String statusName, String type, String release) {
        this.JEP_ID = jepId;
        this.SEARCH_PARAM = searchParam;
        this.STATUS_NAME = statusName;
        this.TYPE = type;
        this.RELEASE = release;
    }

    public JepRequestBuilder jepId(Integer jepId) {
        return new JepRequestBuilder(jepId, SEARCH_PARAM, STATUS_NAME, TYPE, RELEASE);
    }

    public JepRequestBuilder searchParam(String searchParam) {
        return new JepRequestBuilder(JEP_ID, searchParam, STATUS_NAME, TYPE, RELEASE);
    }

    public JepRequestBuilder statusName(String statusName) {
        return new JepRequestBuilder(JEP_ID, SEARCH_PARAM, statusName, TYPE, RELEASE);
    }

    public JepRequestBuilder type(String type) {
        return new JepRequestBuilder(JEP_ID, SEARCH_PARAM, STATUS_NAME, type, RELEASE);
    }

    public JepRequestBuilder release(String release) {
        return new JepRequestBuilder(JEP_ID, SEARCH_PARAM, STATUS_NAME, TYPE, release);
    }

    public JepRequest build() throws ReflectiveOperationException {
        var jepRequest = new JepRequest();
        injectField(jepRequest, "jepId", JEP_ID);
        injectField(jepRequest, "searchParam", SEARCH_PARAM);
        injectField(jepRequest, "statusName", STATUS_NAME);
        injectField(jepRequest, "type", TYPE);
        injectField(jepRequest, "release", RELEASE);
        return jepRequest;
    }

    private void injectField(JepRequest subject, String fieldName, Object value) throws ReflectiveOperationException {
        try {
            var field = subject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(subject, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectiveOperationException(e);
        }
    }

}