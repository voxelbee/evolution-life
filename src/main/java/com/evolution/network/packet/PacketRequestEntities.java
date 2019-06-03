package com.evolution.network.packet;

import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.network.BufferUtils;

import io.netty.buffer.ByteBuf;

public class PacketRequestEntities implements AIPacket
{
  public int numberOfEntities;

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.numberOfEntities = BufferUtils.readVarInt( buf );
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    BufferUtils.writeVarInt( buf, this.numberOfEntities );
  }

  @Override
  public void handlePacket( UUID clientID )
  {
    EvolutionLife.manager.handleClientEntityRequest( this, clientID );
  }
}
