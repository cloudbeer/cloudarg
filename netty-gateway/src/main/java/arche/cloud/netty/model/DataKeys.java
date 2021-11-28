package arche.cloud.netty.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class DataKeys {

    public static AttributeKey<ChannelHandlerContext> PARENT_CONTEXT
            = AttributeKey.newInstance("parentContext");

    public static AttributeKey<Route> API_INFO
            = AttributeKey.newInstance("apiInfo");

    public static AttributeKey<Route> ROUTE_INFO
            = AttributeKey.newInstance("route");

    public static AttributeKey<UserRequest> REQUEST_INFO
            = AttributeKey.newInstance("requestInfo");


    public static AttributeKey<User> USER_INFO
            = AttributeKey.newInstance("userInfo");

}
