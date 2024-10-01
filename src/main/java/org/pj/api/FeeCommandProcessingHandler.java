package org.pj.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.pj.dto.request.FeeCommandDto;
import org.pj.service.IFeeTransactionService;
import org.pj.service.impl.FeeTransactionServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.pj.constant.ResponseMessages.RequestUri.PROCESS_FEE_COMMAND_URI;
import static org.pj.constant.ResponseMessages.PROCESS_SUCCESS_MESSAGE;
import static org.pj.constant.ResponseMessages.RESOURCE_NOT_FOUND;
import static org.pj.utils.HttpResponseUtils.sendHttpResponse;

public class FeeCommandProcessingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final IFeeTransactionService feeTransactionService = new FeeTransactionServiceImpl();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (PROCESS_FEE_COMMAND_URI.equals(request.uri()) && request.method() == HttpMethod.POST) {
            ByteBuf jsonBuf = request.content();
            String json = jsonBuf.toString(StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            FeeCommandDto feeCommandDto = objectMapper.readValue(json, FeeCommandDto.class);

            String responseMessage;
            try {
                feeTransactionService.processFeeCommand(feeCommandDto);
                sendHttpResponse(ctx, PROCESS_SUCCESS_MESSAGE, HttpResponseStatus.OK);
            } catch (Exception e) {
                responseMessage = "{\"status\":500, \"message\":\"" + e.getMessage() + "\"}";
                sendHttpResponse(ctx, responseMessage, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            sendHttpResponse(ctx, RESOURCE_NOT_FOUND, HttpResponseStatus.NOT_FOUND);
        }
    }
}
