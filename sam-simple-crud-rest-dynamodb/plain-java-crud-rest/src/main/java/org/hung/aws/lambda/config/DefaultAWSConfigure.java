package org.hung.aws.lambda.config;

import software.amazon.awssdk.regions.Region;

public interface DefaultAWSConfigure {

    default Region getDefaultRegion() {
        String region = System.getenv("AWS_REGION");
        if (region==null) {
            region = System.getProperty("aws.region");
        }
        return Region.of(region);
    }

    default String getLocalEndpoint() {
        if (Boolean.valueOf(System.getenv("AWS_SAM_LOCAL"))) {
            return "http://localstack-main:4566";
        }
        return System.getProperty("aws.local.endpoint");
    }
}
