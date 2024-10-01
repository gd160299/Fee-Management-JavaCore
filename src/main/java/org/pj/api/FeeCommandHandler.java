package org.pj.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import org.pj.dto.request.FeeCommandDto;
import org.pj.service.IFeeCommandService;
import org.pj.service.impl.FeeCommandServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.pj.constant.ResponseMessages.RequestUri.ADD_FEE_COMMAND_URI;
import static org.pj.constant.ResponseMessages.SUCCESS_MESSAGE;
import static org.pj.utils.HttpResponseUtils.sendHttpResponse;

public class FeeCommandHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final IFeeCommandService feeCommandService = new FeeCommandServiceImpl();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        ctx.channel().attr(AttributeKey.valueOf("requestPath")).set(uri);

        if (ADD_FEE_COMMAND_URI.equals(uri) && request.method() == HttpMethod.POST) {
            ByteBuf jsonBuf = request.content();
            String json = jsonBuf.toString(StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            FeeCommandDto feeCommandDto = objectMapper.readValue(json, FeeCommandDto.class);
            feeCommandService.addFeeCommand(feeCommandDto);
            sendHttpResponse(ctx, SUCCESS_MESSAGE, HttpResponseStatus.OK);
        } else {
            ctx.fireChannelRead(request.retain());
        }
    }
}
