package com.evolution.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.ai.EntityAI;
import com.evolution.network.packet.AIPacket;
import com.evolution.network.packet.PacketClientSettings;
import com.mojang.authlib.GameProfile;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;

public class ClientHandler
{
  private Map< UUID, EntityAI > entities = new HashMap< UUID, EntityAI >();

  private AINetworkManager networkManager;

  public ClientHandler( AINetworkManager inNetworkManager )
  {
    this.networkManager = inNetworkManager;
  }

  /**
   * Sends a packet to the client.
   *
   * @param packet
   */
  public void sendPacket( AIPacket packet )
  {
    this.networkManager.sendPacket( packet );
  }

  public boolean isConnected()
  {
    return this.networkManager.isConnected;
  }

  /**
   * Ticks all the entities owned by this client
   */
  public void tick()
  {
    for ( UUID key : this.entities.keySet() )
    {
      EntityAI ai = this.entities.get( key );
      ai.playerTick();
    }
  }

  /**
   * Creates a new entity and adds them to the game
   *
   * @param name
   * @return
   */
  public UUID createNewEntity( String name )
  {
    EntityAI entity = new EntityAI( EvolutionLife.mcServer,
        EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( UUID.randomUUID(), name ),
        new PlayerInteractionManager( EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ) ) );
    EvolutionLife.mcServer.getPlayerList().initializeConnectionToPlayer( new NetworkManager( EnumPacketDirection.CLIENTBOUND ), entity );
    entity.stepHeight = 0.5f;
    entity.setGameType( GameType.SURVIVAL );
    // entity.onKillCommand(); // Set the player to be ready for spawning
    entities.put( entity.getUniqueID(), entity );
    return entity.getUniqueID();
  }

  /**
   * Removes the entity from the game
   *
   * @param id - The UUID for the entity to remove
   */
  public void removeEntity( UUID id )
  {
    EvolutionLife.mcServer.getPlayerList().playerLoggedOut( entities.get( id ) );
  }

  /**
   * Handles a settings packet from the client. This is usually the first
   * packet to be received. It creates all the entities needed for this client
   *
   * @param packet - The packet to handle
   */
  public void handleClientSettings( PacketClientSettings packet )
  {
    for ( int i = 0; i < packet.entitySimulationCount; i++ )
    {
      this.createNewEntity( "Bob" );
    }
  }
}
