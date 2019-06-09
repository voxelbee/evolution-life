package com.evolution.network;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public class OrganismCountPacket
{
  int numberOfOrganisms;

  public OrganismCountPacket()
  {

  }

  public OrganismCountPacket( int inNumberOfOrganisms )
  {
    this.numberOfOrganisms = inNumberOfOrganisms;
  }

  static public final BiConsumer< OrganismCountPacket, PacketBuffer > ENCODER = ( msg, buffer ) ->
  {
    buffer.writeVarInt( msg.numberOfOrganisms );
  };

  static public final Function< PacketBuffer, OrganismCountPacket > DECODER = ( buffer ) ->
  {
    return new OrganismCountPacket( buffer.readVarInt() );
  };
}
