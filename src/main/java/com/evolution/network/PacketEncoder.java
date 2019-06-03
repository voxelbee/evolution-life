package com.evolution.network;

import java.io.IOException;

import com.evolution.network.packet.AIPacket;
import com.evolution.network.packet.EnumPacketTypes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder< AIPacket >
{
  @Override
  protected void encode( ChannelHandlerContext p_encode_1_, AIPacket p_encode_2_, ByteBuf p_encode_3_ ) throws Exception
  {
    Integer integer = EnumPacketTypes.PacketTypes.getIdFromPacket( p_encode_2_ );

    if ( integer == null )
    {
      throw new IOException( "Can't serialize unregistered packet" );
    }
    else
    {
      BufferUtils.writeVarInt( p_encode_3_, integer );

      try
      {
        p_encode_2_.writePacket( p_encode_3_ );
      }
      catch ( Throwable throwable )
      {
        throw throwable;
      }
    }
  }
}
