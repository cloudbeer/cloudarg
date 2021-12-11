package arche.cloud.netty.utils;

import arche.cloud.netty.client.ColdChannelPool;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcUtil {
    private RpcUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <V> void access(
            String host,
            int port,
            GenericFutureListener<? extends Future<? super Channel>> listener) {

        ColdChannelPool.BOOTSTRAP.remoteAddress(host, port);

        final FixedChannelPool pool = ColdChannelPool.POOLMAP.get(host);
        Future<Channel> future = pool.acquire();
        future.addListener(listener);
    }
}
