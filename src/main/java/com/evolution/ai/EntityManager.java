package com.evolution.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;

public class EntityManager
{
  private Map< UUID, EntityAI > entities = new HashMap< UUID, EntityAI >();
  private MinecraftServer mcServer;

  public EntityManager( MinecraftServer server )
  {
    this.mcServer = server;
  }

  /**
   * Creates a new entity and adds them to the game
   *
   * @param name
   * @return
   */
  public UUID spawnNewEntity( String name )
  {
    EntityAI entity = new EntityAI( this.mcServer,
        this.mcServer.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( UUID.randomUUID(), name ),
        new PlayerInteractionManager( this.mcServer.getWorld( DimensionType.OVERWORLD ) ) );
    this.mcServer.getPlayerList().initializeConnectionToPlayer( new NetworkManager( EnumPacketDirection.CLIENTBOUND ), entity );
    entity.stepHeight = 0.5f;
    entity.setGameType( GameType.SURVIVAL );
    this.entities.put( entity.getUniqueID(), entity );
    return entity.getUniqueID();
  }

  /**
   * Removes the entity from the game
   *
   * @param id - The UUID for the entity to remove
   */
  public void removeEntity( UUID id )
  {
    this.mcServer.getPlayerList().playerLoggedOut( this.entities.remove( id ) );
  }

  public EntityAI getEntity( UUID id )
  {
    return this.entities.get( id );
  }

  /**
   * Updates all the entities
   */
  public void update()
  {
    for ( UUID key : this.entities.keySet() )
    {
      EntityAI entity = this.entities.get( key );
      entity.playerTick();
    }
  }
}
