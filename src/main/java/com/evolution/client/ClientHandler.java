package com.evolution.client;

import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.OrganismCountPacket;

public class ClientHandler
{
  int numberOfOrganisms = 10;

  public void sendOrganismRequest()
  {
    EvolutionLifePacketHandler.INSTANCE.sendToServer( new OrganismCountPacket( numberOfOrganisms ) );
  }
}
