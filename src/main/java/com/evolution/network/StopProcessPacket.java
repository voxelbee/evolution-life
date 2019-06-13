package com.evolution.network;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public class StopProcessPacket
{
  public Set< UUID > organisms;

  public StopProcessPacket()
  {

  }

  public StopProcessPacket( Set< UUID > inOrganisms )
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
    Set< UUID > organisms = new HashSet< UUID >();
    int count = buffer.readVarInt();
    for ( int i = 0; i < count; i++ )
    {
      organisms.add( buffer.readUniqueId() );
    }
    return new StopProcessPacket( organisms );
  };
}
