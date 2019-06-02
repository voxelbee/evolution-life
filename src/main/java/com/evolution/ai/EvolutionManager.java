package com.evolution.ai;

import java.util.UUID;

import com.evolution.network.ServerManager;

import net.minecraft.server.MinecraftServer;

public class EvolutionManager
{
  private MinecraftServer mcServer;
  public ServerManager dispatchServer;

  private EntityManager entityManager;

  public EvolutionManager( MinecraftServer inMcServer, ServerManager server )
  {
    this.mcServer = inMcServer;
    this.dispatchServer = server;

    this.entityManager = new EntityManager( inMcServer );
  }

  /**
   * Called every in game tick. 20tps
   */
  public void tick()
  {
    this.entityManager.update();
  }

  /**
   * Handles a new connection from a client
   *
   * @param aisToSim - The number of ai's that this client can run
   * @param clientID - The id of the client that the packet is from
   */
  public void handleNewConnection( int aisToSim, UUID clientID )
  {
    for ( int i = 0; i < aisToSim; i++ )
    {
      this.entityManager.spawnNewEntity( "Bob" );
    }
  }
}
