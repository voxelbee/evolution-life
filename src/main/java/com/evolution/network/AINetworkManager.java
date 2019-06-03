package com.evolution.network;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.evolution.network.packet.AIPacket;
import com.evolution.network.packet.PacketSocketConnect;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.util.LazyLoadBase;

public class AINetworkManager extends SimpleChannelInboundHandler< AIPacket >
{
  public static final LazyLoadBase< NioEventLoopGroup > CLIENT_NIO_EVENTLOOP = new LazyLoadBase<>( () ->
  {
    return new NioEventLoopGroup( 0, ( new ThreadFactoryBuilder() ).setNameFormat( "AI Client IO #%d" ).setDaemon( true ).build() );
  } );
  public static final LazyLoadBase< EpollEventLoopGroup > CLIENT_EPOLL_EVENTLOOP = new LazyLoadBase<>( () ->
  {
    return new EpollEventLoopGroup( 0, ( new ThreadFactoryBuilder() ).setNameFormat( "AI Client IO #%d" ).setDaemon( true ).build() );
  } );

  private UUID clientID;

  private Channel channel;

  public boolean isConnected = false;

  public static void createServer( String address, int port )
  {
    Class< ? extends ServerChannel > sClass;
    LazyLoadBase< ? extends EventLoopGroup > lazyloadbase;
    if ( Epoll.isAvailable() )
    {
      sClass = EpollServerSocketChannel.class;
      lazyloadbase = CLIENT_EPOLL_EVENTLOOP;
    }
    else
    {
      sClass = NioServerSocketChannel.class;
      lazyloadbase = CLIENT_NIO_EVENTLOOP;
    }

    ( new ServerBootstrap() ).group( lazyloadbase.getValue() ).childHandler( new ChannelInitializer< Channel >()
    {
      @Override
      protected void initChannel( Channel p_initChannel_1_ ) throws Exception
      {
        try
        {
          p_initChannel_1_.config().setOption( ChannelOption.TCP_NODELAY, true );
        }
        catch ( ChannelException var3 )
        {
          ;
        }

        p_initChannel_1_.pipeline()
            .addLast( "timeout", new ReadTimeoutHandler( 30 ) )
            .addLast( "splitter", new FrameDecoder() )
            .addLast( "decoder", new PacketDecoder() )
            .addLast( "prepender", new FrameEncoder() )
            .addLast( "encoder", new PacketEncoder() )
            .addLast( "packet_handler", new AINetworkManager() );
      }
    } ).channel( sClass ).localAddress( new InetSocketAddress( address, port ) ).bind().syncUninterruptibly();
  }

  public void setClientID( UUID inClientID )
  {
    this.clientID = inClientID;
  }

  @Override
  protected void channelRead0( ChannelHandlerContext ctx, AIPacket msg ) throws Exception
  {
    MainThreadPacketHandler.handlePacket( msg, clientID );
  }

  public void sendPacket( AIPacket packetIn )
  {
    if ( this.channel.eventLoop().inEventLoop() )
    {

      ChannelFuture channelfuture = this.channel.writeAndFlush( packetIn );
      channelfuture.addListener( ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE );
    }
    else
    {
      this.channel.eventLoop().execute( () ->
      {
        ChannelFuture channelfuture1 = this.channel.writeAndFlush( packetIn );
        channelfuture1.addListener( ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE );
      } );
    }
  }

  @Override
  public void channelActive( ChannelHandlerContext p_channelActive_1_ ) throws Exception
  {
    super.channelActive( p_channelActive_1_ );
    this.channel = p_channelActive_1_.channel();
    this.isConnected = true;
    MainThreadPacketHandler.handlePacket( new PacketSocketConnect( this ), clientID );
  }
}
