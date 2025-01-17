package org.hung.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

public class MyLambdaHandler implements RequestHandler<Request,String> {

    @Override    
    public String handleRequest(Request request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("received request in MyLambdaHandler.handleRequest", LogLevel.INFO);
        return "Hello " + request.name() + " !!!";
    }

}

record Request(String name) {}
