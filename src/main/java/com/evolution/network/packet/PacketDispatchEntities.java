package com.evolution.network.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.evolution.network.BufferUtils;

import io.netty.buffer.ByteBuf;

public class PacketDispatchEntities implements AIPacket
{
  public List< UUID > entityIds;

  public PacketDispatchEntities()
  {

  }

  public PacketDispatchEntities( List< UUID > entityIds )
  {
    this.entityIds = entityIds;
  }

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.entityIds = new ArrayList< UUID >();

    int count = BufferUtils.readVarInt( buf );
    for ( int i = 0; i < count; i++ )
    {
      this.entityIds.add( BufferUtils.readUniqueId( buf ) );
    }
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    BufferUtils.writeVarInt( buf, entityIds.size() );
    for ( UUID uuid : entityIds )
    {
      BufferUtils.writeUniqueId( buf, uuid );
    }
  }

  @Override
  public void handlePacket( UUID clientID )
  {

  }
}
