package org.pj.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Service.IFeeCommandService;
import org.pj.Service.Impl.FeeCommandServiceImpl;

import java.nio.charset.StandardCharsets;

public class FeeCommandHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final IFeeCommandService feeCommandService = new FeeCommandServiceImpl();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        ctx.channel().attr(AttributeKey.valueOf("requestPath")).set(uri);

        if ("/api/fee-commands/add".equals(uri) && request.method() == HttpMethod.POST) {
            ByteBuf jsonBuf = request.content();
            String json = jsonBuf.toString(StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            FeeCommandDto feeCommandDto = objectMapper.readValue(json, FeeCommandDto.class);

            String responseMessage;
            feeCommandService.addFeeCommand(feeCommandDto);
            responseMessage = "{\"status\":200, \"message\":\"Fee command added successfully.\"}";
            sendSuccessResponse(ctx, responseMessage);
        } else {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void sendSuccessResponse(ChannelHandlerContext ctx, String responseMessage) {
        ByteBuf content = ctx.alloc().buffer();
        content.writeBytes(responseMessage.getBytes(StandardCharsets.UTF_8));
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
