package com.evolution.network;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.evolution.network.packet.AIPacket;

public class MainThreadPacketHandler
{
  private static BlockingQueue< ClientPacketHolder > packetQueue = new LinkedBlockingQueue< ClientPacketHolder >();
  private static int maxPacketProcessing = 2000;

  public static void handlePacket( AIPacket packet, UUID clientID )
  {
    try
    {
      packetQueue.put( new ClientPacketHolder( packet, clientID ) );
    }
    catch ( InterruptedException e )
    {
      e.printStackTrace();
    }
  }

  public static void tick()
  {
    for ( int i = 0; i < maxPacketProcessing; i++ )
    {
      ClientPacketHolder packetHolder = packetQueue.poll();
      if ( packetHolder != null )
      {
        packetHolder.packet.handlePacket( packetHolder.clientID );
      }
      else
      {
        break;
      }
    }
  }

  public static class ClientPacketHolder
  {
    public AIPacket packet;
    public UUID clientID;

    public ClientPacketHolder( AIPacket inPacket, UUID inClientID )
    {
      this.packet = inPacket;
      this.clientID = inClientID;
    }
  }
}
