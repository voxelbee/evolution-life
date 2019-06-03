package com.evolution.network.packet;

import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.network.BufferUtils;

import io.netty.buffer.ByteBuf;

public class PacketClientSettings implements AIPacket
{
  public int entitySimulationCount;

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.entitySimulationCount = BufferUtils.readVarInt( buf );
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    BufferUtils.writeVarInt( buf, this.entitySimulationCount );
  }

  @Override
  public void handlePacket( UUID clientID )
  {
    EvolutionLife.manager.getClientHandler( clientID ).handleClientSettings( this );
  }
}
