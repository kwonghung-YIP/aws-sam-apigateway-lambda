package org.hung.aws.lambda.function.authorizer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PolicyDocument {

    @JsonProperty("Version")
    private String version = "2012-10-17";

    @JsonProperty("Statement")
    private List<Statement> statement;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public List<Statement> getStatement() {
        return statement;
    }
    public void setStatement(List<Statement> statement) {
        this.statement = statement;
    }

}
