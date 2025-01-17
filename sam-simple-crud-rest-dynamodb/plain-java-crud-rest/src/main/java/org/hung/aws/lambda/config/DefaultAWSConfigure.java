package org.hung.aws.lambda.config;

import software.amazon.awssdk.regions.Region;

public interface DefaultAWSConfigure {

    default Region getDefaultRegion() {
        return Region.of(System.getenv("AWS_REGION"));
    }

    default boolean isSamLocal() {
        return Boolean.valueOf(System.getenv("AWS_SAM_LOCAL"));
    }
}
