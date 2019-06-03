package com.evolution.network.packet;

import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.network.AINetworkManager;

import io.netty.buffer.ByteBuf;

public class PacketSocketConnect implements AIPacket
{
  public AINetworkManager netManager;

  public PacketSocketConnect( AINetworkManager inNetManager )
  {
    this.netManager = inNetManager;
  }

  @Override
  public void readPacket( ByteBuf buf )
  {

  }

  @Override
  public void writePacket( ByteBuf buf )
  {

  }

  @Override
  public void handlePacket( UUID clientID )
  {
    EvolutionLife.manager.handleSocketConnect( this );
  }
}
