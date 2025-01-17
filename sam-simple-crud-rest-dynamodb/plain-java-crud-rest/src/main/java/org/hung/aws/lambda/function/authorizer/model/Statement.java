package org.hung.aws.lambda.function.authorizer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statement {

    @JsonProperty("Action")
    private String action;

    @JsonProperty("Effect")
    private String effect;

    @JsonProperty("Resource")
    private List<String> resource;

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getEffect() {
        return effect;
    }
    public void setEffect(String effect) {
        this.effect = effect;
    }

    public List<String> getResource() {
        return resource;
    }
    public void setResource(List<String> resource) {
        this.resource = resource;
    }

}
