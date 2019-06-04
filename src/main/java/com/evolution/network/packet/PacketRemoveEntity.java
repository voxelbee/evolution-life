package com.evolution.network.packet;

import java.util.UUID;

import com.evolution.network.BufferUtils;

import io.netty.buffer.ByteBuf;

public class PacketRemoveEntity implements AIPacket
{
  public UUID entity;

  public PacketRemoveEntity()
  {

  }

  public PacketRemoveEntity( UUID idIn )
  {
    this.entity = idIn;
  }

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.entity = BufferUtils.readUniqueId( buf );
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    BufferUtils.writeUniqueId( buf, this.entity );
  }

  @Override
  public void handlePacket( UUID clientID )
  {

  }
}
