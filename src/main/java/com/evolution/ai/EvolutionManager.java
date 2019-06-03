package com.evolution.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.network.AINetworkManager;
import com.evolution.network.ClientHandler;
import com.evolution.network.packet.PacketConnectionSuccess;
import com.evolution.network.packet.PacketSocketConnect;

public class EvolutionManager
{
  private DNAGenerator breeder;

  private Map< UUID, ClientHandler > clients = new HashMap< UUID, ClientHandler >();

  public EvolutionManager()
  {
    AINetworkManager.createServer( EvolutionLife.ADDRESS, EvolutionLife.PORT, UUID.randomUUID() );

    this.breeder = new DNAGenerator();
    Thread threadBreeder = new Thread( this.breeder );
    threadBreeder.setName( "DNA Generator" );
    threadBreeder.start();
  }

  public void close()
  {
    this.breeder.stop();
  }

  /**
   * Called every in game tick. 20tps
   */
  public void tick()
  {
    for ( UUID key : this.clients.keySet() )
    {
      this.clients.get( key ).tick();
    }
  }

  /**
   * Handles the connection of a new client by creating the handler and adding it to the clients
   * list.
   *
   * @param packet - Packet to handle
   */
  public void handleSocketConnect( PacketSocketConnect packet )
  {
    UUID id = UUID.randomUUID();
    packet.netManager.setClientID( id );
    clients.put( id, new ClientHandler( packet.netManager ) );
    clients.get( id ).sendPacket( new PacketConnectionSuccess() );
    System.out.println( "Client connected..." );
  }

  public ClientHandler getClientHandler( UUID id )
  {
    return this.clients.get( id );
  }
}
