package com.evolution.network.packet;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

public class DNAPacket implements AIPacket
{
  public byte[] DNA;
  public UUID aiID;

  public DNAPacket( byte[] inDna, UUID inAIID )
  {
    this.DNA = inDna;
    this.aiID = inAIID;
  }

  public DNAPacket()
  {

  }

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.aiID = new UUID( buf.readLong(), buf.readLong() );
    this.DNA = new byte[ buf.readInt() ];
    buf.readBytes( DNA );
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    buf.writeLong( this.aiID.getLeastSignificantBits() );
    buf.writeLong( this.aiID.getMostSignificantBits() );
    buf.writeInt( DNA.length );
    buf.writeBytes( DNA );
  }

  @Override
  public void handlePacket( UUID clientID )
  {

  }
}
