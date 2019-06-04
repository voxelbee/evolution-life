package com.evolution.network.packet;

import java.util.UUID;

import com.evolution.network.BufferUtils;

import io.netty.buffer.ByteBuf;

public class PacketDispatchEntity implements AIPacket
{
  public UUID entityId;
  public byte[] entityDna;

  public PacketDispatchEntity()
  {

  }

  public PacketDispatchEntity( UUID entityId, byte[] entityDna )
  {
    this.entityId = entityId;
    this.entityDna = entityDna;
  }

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.entityId = BufferUtils.readUniqueId( buf );

    byte[] dna = new byte[ BufferUtils.readVarInt( buf ) ];
    buf.readBytes( dna );
    this.entityDna = dna;
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    BufferUtils.writeUniqueId( buf, this.entityId );
    BufferUtils.writeVarInt( buf, this.entityDna.length );
    buf.writeBytes( this.entityDna );
  }

  @Override
  public void handlePacket( UUID clientID )
  {

  }
}
