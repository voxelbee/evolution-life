package com.evolution.network;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public class RequestProcessPacket
{
  public UUID organism;
  public byte[] dna;

  public RequestProcessPacket()
  {

  }

  public RequestProcessPacket( UUID inOrganism, byte[] inDna )
  {
    this.organism = inOrganism;
    this.dna = inDna;
  }

  static public final BiConsumer< RequestProcessPacket, PacketBuffer > ENCODER = ( msg, buffer ) ->
  {
    buffer.writeUniqueId( msg.organism );
    buffer.writeByteArray( msg.dna );
  };

  static public final Function< PacketBuffer, RequestProcessPacket > DECODER = ( buffer ) ->
  {
    UUID organism = buffer.readUniqueId();
    byte[] dna = buffer.readByteArray();
    return new RequestProcessPacket( organism, dna );
  };
}
