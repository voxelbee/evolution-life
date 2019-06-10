package com.evolution.server;

import java.util.UUID;

import com.evolution.EvolutionLife;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.DimensionType;

public class EntityOrganism extends EntityPlayerMP
{
  public boolean processed;
  public int skippedTicks = 0;

  public EntityOrganism( UUID entityID, String name )
  {
    super( EvolutionLife.mcServer,
        EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( entityID, name ),
        new PlayerInteractionManager( EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ) ) );
    this.stepHeight = 0.5f;
  }

  public void setAiMovement( float forward, float strafe )
  {
    this.moveForward = MathHelper.clamp( forward, -0.5f, 0.5f );
    this.moveStrafing = MathHelper.clamp( strafe, -0.5f, 0.5f );
  }

  public void setAiRotation( float yaw, float pitch )
  {
    this.rotationPitch = pitch % 360;
    this.rotationYaw = yaw % 360;
  }

  public void addToWorld()
  {
    GameProfile gameprofile = this.getGameProfile();
    PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
    playerprofilecache.addEntry( gameprofile );
    NBTTagCompound nbttagcompound = this.server.getPlayerList().readPlayerDataFromFile( this );

    // Forge: Make sure the dimension hasn't been deleted, if so stick them in the overworld.
    WorldServer playerWorld = this.server.getWorld( this.dimension );
    if ( playerWorld == null )
    {
      this.dimension = DimensionType.OVERWORLD;
      playerWorld = this.server.getWorld( this.dimension );
      this.setPosition( playerWorld.getWorldInfo().getSpawnX(), playerWorld.getWorldInfo().getSpawnY(), playerWorld.getWorldInfo().getSpawnZ() );
    }

    this.setWorld( playerWorld );
    this.interactionManager.setWorld( (WorldServer) this.world );

    WorldServer worldserver = this.server.getWorld( this.dimension );
    this.interactionManager.setGameType( GameType.SURVIVAL );
    NetHandlerPlayServer nethandlerplayserver = new PlayServerWrapper( this.server, this );
    this.server.getPlayerList().updatePermissionLevel( this );
    this.getStats().markAllDirty();
    this.getRecipeBook().init( this );
    this.server.refreshStatusNextTick();
    this.server.getPlayerList().playerLoggedIn( this );
    nethandlerplayserver.setPlayerLocation( this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch );
    this.server.getPlayerList().sendWorldInfo( this, worldserver );

    for ( PotionEffect potioneffect : this.getActivePotionEffects() )
    {
      nethandlerplayserver.sendPacket( new SPacketEntityEffect( this.getEntityId(), potioneffect ) );
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
          this.startRiding( entity1, true );
        }
        else
        {
          for ( Entity entity : entity1.getRecursivePassengers() )
          {
            if ( entity.getUniqueID().equals( uuid ) )
            {
              this.startRiding( entity, true );
              break;
            }
          }
        }

        if ( !this.isPassenger() )
        {
          worldserver.removeEntityDangerously( entity1 );

          for ( Entity entity2 : entity1.getRecursivePassengers() )
          {
            worldserver.removeEntityDangerously( entity2 );
          }
        }
      }
    }

    this.addSelfToInternalCraftingInventory();
    this.interactionManager.setGameType( GameType.SURVIVAL );
  }

  public void removeFromWorld()
  {
    this.server.getPlayerList().playerLoggedOut( this );
  }
}
