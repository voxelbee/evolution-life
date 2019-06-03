package com.evolution.network;

import java.io.IOException;
import java.util.List;

import com.evolution.network.packet.AIPacket;
import com.evolution.network.packet.EnumPacketTypes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder
{
  @Override
  protected void decode( ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List< Object > p_decode_3_ ) throws Exception
  {
    if ( p_decode_2_.readableBytes() != 0 )
    {
      int i = BufferUtils.readVarInt( p_decode_2_ );
      AIPacket packet = EnumPacketTypes.PacketTypes.getPacketFromID( i );

      if ( packet == null )
      {
        throw new IOException( "Bad packet id " + i );
      }
      else
      {
        packet.readPacket( p_decode_2_ );
        if ( p_decode_2_.readableBytes() > 0 )
        {
          throw new IOException( "Packet (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + p_decode_2_.readableBytes()
              + " bytes extra whilst reading packet " + i );
        }
        else
        {
          p_decode_3_.add( packet );
        }
      }
    }
  }
}
