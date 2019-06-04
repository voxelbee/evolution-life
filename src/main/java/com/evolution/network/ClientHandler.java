package com.evolution.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.ai.EntityAI;
import com.evolution.network.packet.AIPacket;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;

public class ClientHandler
{
  private Map< UUID, EntityAI > processingEntities = new HashMap< UUID, EntityAI >();
  private AINetworkManager networkManager;

  public ClientHandler( AINetworkManager inNetworkManager )
  {
    this.networkManager = inNetworkManager;
  }

  public void sendPacket( AIPacket packet )
  {
    this.networkManager.sendPacket( packet );
  }

  public boolean isConnected()
  {
    return this.networkManager.isConnected();
  }

  public void tick()
  {
    for ( UUID key : this.processingEntities.keySet() )
    {
      EntityAI ai = this.processingEntities.get( key );
      ai.playerTick();
    }
  }

  public void addNewEntity( EntityAI entityAI )
  {
    GameProfile gameprofile = entityAI.getGameProfile();
    PlayerProfileCache playerprofilecache = EvolutionLife.mcServer.getPlayerProfileCache();
    GameProfile gameprofile1 = playerprofilecache.getProfileByUUID( gameprofile.getId() );
    String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
    playerprofilecache.addEntry( gameprofile );
    NBTTagCompound nbttagcompound = EvolutionLife.mcServer.getPlayerList().readPlayerDataFromFile( entityAI );

    // Forge: Make sure the dimension hasn't been deleted, if so stick them in the overworld.
    WorldServer playerWorld = EvolutionLife.mcServer.getWorld( entityAI.dimension );
    if ( playerWorld == null )
    {
      entityAI.dimension = DimensionType.OVERWORLD;
      playerWorld = EvolutionLife.mcServer.getWorld( entityAI.dimension );
      entityAI.setPosition( playerWorld.getWorldInfo().getSpawnX(), playerWorld.getWorldInfo().getSpawnY(), playerWorld.getWorldInfo().getSpawnZ() );
    }

    entityAI.setWorld( playerWorld );
    entityAI.interactionManager.setWorld( (WorldServer) entityAI.world );

    WorldServer worldserver = EvolutionLife.mcServer.getWorld( entityAI.dimension );
    WorldInfo worldinfo = worldserver.getWorldInfo();
    entityAI.interactionManager.setGameType( GameType.SURVIVAL );
    NetHandlerPlayServer nethandlerplayserver = new PlayServerWrapper( EvolutionLife.mcServer, entityAI );
    EvolutionLife.mcServer.getPlayerList().updatePermissionLevel( entityAI );
    entityAI.getStats().markAllDirty();
    entityAI.getRecipeBook().init( entityAI );
    EvolutionLife.mcServer.refreshStatusNextTick();
    EvolutionLife.mcServer.getPlayerList().playerLoggedIn( entityAI );
    nethandlerplayserver.setPlayerLocation( entityAI.posX, entityAI.posY, entityAI.posZ, entityAI.rotationYaw, entityAI.rotationPitch );
    EvolutionLife.mcServer.getPlayerList().sendWorldInfo( entityAI, worldserver );

    for ( PotionEffect potioneffect : entityAI.getActivePotionEffects() )
    {
      nethandlerplayserver.sendPacket( new SPacketEntityEffect( entityAI.getEntityId(), potioneffect ) );
    }

    if ( nbttagcompound != null && nbttagcompound.contains( "RootVehicle", 10 ) )
    {
      NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound( "RootVehicle" );
      Entity entity1 = AnvilChunkLoader.readWorldEntity( nbttagcompound1.getCompound( "Entity" ), worldserver, true );
      if ( entity1 != null )
      {
        UUID uuid = nbttagcompound1.getUniqueId( "Attach" );
        if ( entity1.getUniqueID().equals( uuid ) )
        {
          entityAI.startRiding( entity1, true );
        }
        else
        {
          for ( Entity entity : entity1.getRecursivePassengers() )
          {
            if ( entity.getUniqueID().equals( uuid ) )
            {
              entityAI.startRiding( entity, true );
              break;
            }
          }
        }

        if ( !entityAI.isPassenger() )
        {
          worldserver.removeEntityDangerously( entity1 );

          for ( Entity entity2 : entity1.getRecursivePassengers() )
          {
            worldserver.removeEntityDangerously( entity2 );
          }
        }
      }
    }

    entityAI.addSelfToInternalCraftingInventory();
    entityAI.interactionManager.setGameType( GameType.SURVIVAL );
    processingEntities.put( entityAI.getUniqueID(), entityAI );
  }

  public void removeEntity( UUID id )
  {
    EvolutionLife.mcServer.getPlayerList().playerLoggedOut( processingEntities.remove( id ) );
  }

  public void removeAllEntites()
  {
    for ( Iterator< EntityAI > iterator = this.processingEntities.values().iterator(); iterator.hasNext(); )
    {
      EntityAI entity = iterator.next();
      // EvolutionLife.mcServer.getPlayerList().playerLoggedOut( entity );
    }
  }

  public Collection< EntityAI > getAllEntites()
  {
    return this.processingEntities.values();
  }

  public void close()
  {
    this.networkManager.close();
  }
}
