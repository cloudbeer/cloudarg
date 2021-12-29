package arche.cloud.netty.client;

import arche.cloud.netty.model.Route;
import arche.cloud.netty.model.User;
import arche.cloud.netty.model.UserRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;

public class ProxyInitializer extends ChannelInitializer<SocketChannel> {

  private ChannelHandlerContext parentContext;
  private FullHttpRequest parentRequest;
  private UserRequest userRequest;
  private Route route;
  private User user;

  public ChannelHandlerContext getParentContext() {
    return parentContext;
  }

  public void setParentContext(ChannelHandlerContext parentContext) {
    this.parentContext = parentContext;
  }

  public UserRequest getUserRequest() {
    return userRequest;
  }

  public void setUserRequest(UserRequest userRequest) {
    this.userRequest = userRequest;
  }

  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  public FullHttpRequest getParentRequest() {
    return parentRequest;
  }

  public void setParentRequest(FullHttpRequest parentRequest) {
    this.parentRequest = parentRequest;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    // TODO Auto-generated method stub

  }

}
