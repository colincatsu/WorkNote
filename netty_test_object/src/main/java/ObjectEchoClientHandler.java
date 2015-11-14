import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2015/11/14.
 */
public class ObjectEchoClientHandler extends ChannelHandlerAdapter {

    private final List<String> firstMessage;

    /**
     * Creates a client-side handler.
     */
    public ObjectEchoClientHandler() {
        firstMessage = new ArrayList<String>(ObjectEchoClient.SIZE);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 20000; i ++) {
            stringBuilder.append("大家好--"+i+"\n");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test",stringBuilder.toString());
        firstMessage.add(jsonObject.toJSONString());

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.
        ctx.writeAndFlush(firstMessage.get(0));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        JSONObject jsonObject = JSONObject.parseObject((String)msg);
        // Echo back the received object to the client.
        System.out.print(jsonObject.getString("test"));
        // Echo back the received object to the server.
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

