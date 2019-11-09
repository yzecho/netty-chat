package io.yzecho.nettychat.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

/**
 * @author yecho
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {

            FullHttpRequest request = (FullHttpRequest) msg;

            // 检查请求
            if (!request.decoderResult().isSuccess() || !"websocket".equals(request.headers().get("Upgrade"))) {

                // 返回错误信息
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
                if (response.status().code() != 200) {
                    ByteBuf byteFul = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
                    response.content().writeBytes(byteFul);
                    byteFul.release();
                }
                ChannelFuture f = ctx.channel().writeAndFlush(response);
                if (response.status().code() != 200) {
                    f.addListener(ChannelFutureListener.CLOSE);
                }
            }

            WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:8888/websocket", null, false);
            handshaker = webSocketServerHandshakerFactory.newHandshaker(request);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), request);
            }
        } else if (msg instanceof WebSocketFrame) {

            // 判断是否是关闭链路命令
            if (msg instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) msg);
            }
            // 我们只处理文本内容
            if (msg instanceof TextWebSocketFrame) {
                // 获取文本
                String content = ((TextWebSocketFrame) msg).text();
                // 回写
                ctx.writeAndFlush(new TextWebSocketFrame("返回数据: " + msg));
            }
        }
    }
}
