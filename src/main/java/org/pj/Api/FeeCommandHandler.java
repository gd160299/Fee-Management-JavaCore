package org.pj.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Service.IFeeCommandService;
import org.pj.Service.Impl.FeeCommandServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.pj.Utils.HttpResponseUtils.sendHttpResponse;

public class FeeCommandHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final IFeeCommandService feeCommandService = new FeeCommandServiceImpl();

    private static final String ADD_FEE_COMMAND_URI = "/api/fee-commands/add";
    private static final String SUCCESS_MESSAGE = "{\"status\":200, \"message\":\"Fee command added successfully.\"}";
    private static final String RESOURCE_NOT_FOUND = "{\"status\":404, \"message\":\"Resource not found\"}";

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
            sendHttpResponse(ctx, RESOURCE_NOT_FOUND, HttpResponseStatus.NOT_FOUND);
        }
    }
}
