package com.evolution.ai;

import java.util.UUID;

import com.evolution.network.Client;
import com.evolution.network.ServerManager;

public class EvolutionManager
{
  public ServerManager dispatchServer;
  private DNAGenerator breeder;

  public EvolutionManager( ServerManager server )
  {
    this.dispatchServer = server;

    this.breeder = new DNAGenerator();
    Thread threadBreeder = new Thread( this.breeder );
    threadBreeder.setName( "DNA Generator" );
    threadBreeder.start();
  }

  public void close()
  {
    this.dispatchServer.close();
    this.breeder.stop();
  }

  /**
   * Called every in game tick. 20tps
   */
  public void tick()
  {
    this.dispatchServer.tickClients();
  }

  /**
   * Handles a new connection from a client. Creates the new entities that the client
   * will control and then dispatches the generation of new DNA for these ai's.
   *
   * @param aisToSim - The number of ai's that this client can run
   * @param clientID - The id of the client that the packet is from
   */
  public void handleNewConnection( int aisToSim, UUID clientID )
  {
    Client client = this.dispatchServer.getClient( clientID );
    for ( int i = 0; i < aisToSim; i++ )
    {
      client.createNewEntity( "Bob" );
    }

    for ( UUID key : client.getUUIDs() )
    {
      this.breeder.addNewRandom( key );
    }
  }
}
