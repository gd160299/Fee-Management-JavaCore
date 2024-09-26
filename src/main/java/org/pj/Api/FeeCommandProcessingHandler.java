package org.pj.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Service.IFeeTransactionService;
import org.pj.Service.Impl.FeeTransactionServiceImpl;

import java.nio.charset.StandardCharsets;

public class FeeCommandProcessingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final IFeeTransactionService feeTransactionService = new FeeTransactionServiceImpl();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if ("/api/fee-commands/process".equals(request.uri()) && request.method() == HttpMethod.POST) {
            ByteBuf jsonBuf = request.content();
            String json = jsonBuf.toString(StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            FeeCommandDto feeCommandDto = objectMapper.readValue(json, FeeCommandDto.class);

            String responseMessage;
            try {
                feeTransactionService.processFeeCommand(feeCommandDto);
                responseMessage = "{\"status\":200, \"message\":\"Fee command is being processed.\"}";
            } catch (Exception e) {
                responseMessage = "{\"status\":500, \"message\":\"" + e.getMessage() + "\"}";
            }

            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes(responseMessage.getBytes(StandardCharsets.UTF_8));
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
