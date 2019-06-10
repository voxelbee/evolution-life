package com.evolution.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public class StopProcessPacket
{
  public List< UUID > organisms;

  public StopProcessPacket()
  {

  }

  public StopProcessPacket( List< UUID > inOrganisms )
  {
    this.organisms = inOrganisms;
  }

  static public final BiConsumer< StopProcessPacket, PacketBuffer > ENCODER = ( msg, buffer ) ->
  {
    buffer.writeVarInt( msg.organisms.size() );
    for ( UUID item : msg.organisms )
    {
      buffer.writeUniqueId( item );
    }
  };

  static public final Function< PacketBuffer, StopProcessPacket > DECODER = ( buffer ) ->
  {
    List< UUID > organisms = new ArrayList< UUID >();
    int count = buffer.readVarInt();
    for ( int i = 0; i < count; i++ )
    {
      organisms.add( buffer.readUniqueId() );
    }
    return new StopProcessPacket( organisms );
  };
}
