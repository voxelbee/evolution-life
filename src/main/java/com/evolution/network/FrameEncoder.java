package com.evolution.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class FrameEncoder extends MessageToByteEncoder< ByteBuf >
{
  @Override
  protected void encode( ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_ ) throws Exception
  {
    int i = p_encode_2_.readableBytes();
    int j = BufferUtils.getVarIntSize( i );
    if ( j > 3 )
    {
      throw new IllegalArgumentException( "unable to fit " + i + " into " + 3 );
    }
    else
    {
      p_encode_3_.ensureWritable( j + i );
      BufferUtils.writeVarInt( p_encode_3_, i );
      p_encode_3_.writeBytes( p_encode_2_, p_encode_2_.readerIndex(), i );
    }
  }
}
