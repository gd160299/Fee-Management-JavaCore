package org.pj.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof BusinessException ex) {
            sendErrorResponse(ctx, ex.getCode(), ex.getMessage(), ex.getTimestamp());
        } else {
            logger.error("Unhandled exception", cause);
            sendErrorResponse(ctx, 500, "Internal Server Error", null);
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, int statusCode, String message, LocalDateTime timestamp) {
        try {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("timestamp", timestamp != null ? timestamp.toString() : LocalDateTime.now().toString());
            responseBody.put("status", statusCode);
            responseBody.put("error", message);
            responseBody.put("path", ctx.channel().attr(AttributeKey.valueOf("requestPath")).get());

            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(responseBody);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(statusCode),
                    Unpooled.copiedBuffer(responseJson, StandardCharsets.UTF_8));

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            logger.error("Error sending error response", e);
            ctx.close();
        }
    }
}
