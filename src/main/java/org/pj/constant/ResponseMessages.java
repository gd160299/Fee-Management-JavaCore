package org.pj.constant;

public class ResponseMessages {
    public static final String SUCCESS_MESSAGE = "{\"status\":200, \"message\":\"Fee command added successfully.\"}";
    public static final String PROCESS_SUCCESS_MESSAGE = "{\"status\":200, \"message\":\"Fee command is being processed.\"}";
    public static final String RESOURCE_NOT_FOUND = "{\"status\":404, \"message\":\"Resource not found\"}";

    public static class RequestUri {
        public static final String ADD_FEE_COMMAND_URI = "/api/fee-commands/add";
        public static final String PROCESS_FEE_COMMAND_URI = "/api/fee-commands/process";
    }
}
