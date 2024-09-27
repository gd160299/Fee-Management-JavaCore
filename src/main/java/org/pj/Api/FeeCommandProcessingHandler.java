package org.pj.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Service.IFeeTransactionService;
import org.pj.Service.Impl.FeeTransactionServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.pj.Utils.HttpResponseUtils.sendHttpResponse;

public class FeeCommandProcessingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final IFeeTransactionService feeTransactionService = new FeeTransactionServiceImpl();

    private static final String PROCESS_FEE_COMMAND_URI = "/api/fee-commands/process";
    private static final String SUCCESS_MESSAGE = "{\"status\":200, \"message\":\"Fee command is being processed.\"}";
    private static final String RESOURCE_NOT_FOUND = "{\"status\":404, \"message\":\"Resource not found\"}";

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
                sendHttpResponse(ctx, SUCCESS_MESSAGE, HttpResponseStatus.OK);
            } catch (Exception e) {
                responseMessage = "{\"status\":500, \"message\":\"" + e.getMessage() + "\"}";
                sendHttpResponse(ctx, responseMessage, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            sendHttpResponse(ctx, RESOURCE_NOT_FOUND, HttpResponseStatus.NOT_FOUND);
        }
    }



}
