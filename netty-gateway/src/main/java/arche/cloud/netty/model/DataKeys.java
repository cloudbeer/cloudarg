package arche.cloud.netty.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class DataKeys {
        private DataKeys() {
                throw new IllegalStateException("Utility class");
        }

        public static final AttributeKey<ChannelHandlerContext> PARENT_CONTEXT = AttributeKey
                        .newInstance("parentContext");

        public static final AttributeKey<Route> API_INFO = AttributeKey.newInstance("apiInfo");

        public static final AttributeKey<Route> ROUTE_INFO = AttributeKey.newInstance("route");

        public static final AttributeKey<UserRequest> REQUEST_INFO = AttributeKey.newInstance("requestInfo");

        public static final AttributeKey<User> USER_INFO = AttributeKey.newInstance("userInfo");

        public static final AttributeKey<String> BACKEND = AttributeKey.newInstance("backend");

}
