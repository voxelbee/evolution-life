package com.evolution.network;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import com.evolution.network.packet.AIPacket;

public class MainThreadPacketHandler
{
  private static Queue< ClientPacketHolder > packetQueue = new PriorityQueue< ClientPacketHolder >();
  private static int maxPacketProcessing = 2000;

  public static void handlePacket( AIPacket packet, UUID clientID )
  {
    packetQueue.add( new ClientPacketHolder( packet, clientID ) );
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
