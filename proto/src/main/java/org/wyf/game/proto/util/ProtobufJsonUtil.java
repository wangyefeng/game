package org.wyf.game.proto.util;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

public class ProtobufJsonUtil {

    private static class ProtobufJsonUtilHolder {
        private static final JsonFormat.Printer PRINTER = JsonFormat.printer().alwaysPrintFieldsWithNoPresence();
    }

    public static String serializeMessage(Message message) {
        try {
            return ProtobufJsonUtilHolder.PRINTER.print(message);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
