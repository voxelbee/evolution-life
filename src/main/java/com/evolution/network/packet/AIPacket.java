package com.evolution.network.packet;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

public interface AIPacket
{
  public void readPacket( ByteBuf buf );

  public void writePacket( ByteBuf buf );

  public void handlePacket( UUID clientID );
}
