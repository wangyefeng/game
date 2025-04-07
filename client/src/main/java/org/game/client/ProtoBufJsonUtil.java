package org.game.client;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

public class ProtoBufJsonUtil {

    private static final ThreadLocal<JsonFormat.Printer> printerThreadLocal =
        ThreadLocal.withInitial(() -> JsonFormat.printer().includingDefaultValueFields());

    public static String serializeMessage(Message message) {
        JsonFormat.Printer printer = printerThreadLocal.get();
        try {
            return printer.print(message);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
