package org.hung.aws.lambda.function.authorizer.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AuthorizerResponse {

    private String principalId;
    private PolicyDocument policyDocument;
    private Map<String,Object> context;
    private String usageIdentifierKey;

    public String getPrincipalId() {
        return principalId;
    }
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
    
    public PolicyDocument getPolicyDocument() {
        return policyDocument;
    }
    public void setPolicyDocument(PolicyDocument policyDocument) {
        this.policyDocument = policyDocument;
    }
    
    public Map<String, Object> getContext() {
        return context;
    }
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
    
    public String getUsageIdentifierKey() {
        return usageIdentifierKey;
    }
    public void setUsageIdentifierKey(String usageIdentifierKey) {
        this.usageIdentifierKey = usageIdentifierKey;
    }

}
