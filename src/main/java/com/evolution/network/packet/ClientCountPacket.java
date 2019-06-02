package com.evolution.network.packet;

import java.util.UUID;

import com.evolution.EvolutionLife;

import io.netty.buffer.ByteBuf;

public class ClientCountPacket implements AIPacket
{
  public int AICount;

  public ClientCountPacket( int count )
  {
    this.AICount = count;
  }

  public ClientCountPacket()
  {

  }

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.AICount = buf.readInt();
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    buf.writeInt( this.AICount );
  }

  @Override
  public void handlePacket( UUID clientID )
  {
    EvolutionLife.manager.handleNewConnection( this.AICount, clientID );
  }
}
