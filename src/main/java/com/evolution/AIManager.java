package com.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.evolution.network.ServerManager;
import com.mojang.authlib.GameProfile;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;

public class AIManager
{
  private List< EntityAI > ais = new ArrayList< EntityAI >();
  private MinecraftServer mcServer;
  public int spawnCount = 0;
  public ServerManager server;

  public AIManager( MinecraftServer server )
  {
    this.mcServer = server;
  }

  public void createNewAI( String name )
  {
    EntityAI player = new EntityAI( mcServer,
        mcServer.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( UUID.randomUUID(), name ),
        new PlayerInteractionManager( mcServer.getWorld( DimensionType.OVERWORLD ) ) );
    mcServer.getPlayerList().initializeConnectionToPlayer( new NetworkManager( EnumPacketDirection.CLIENTBOUND ), player );
    player.stepHeight = 0.5f;
    player.setGameType( GameType.SURVIVAL );
    ais.add( player );
  }

  public void update()
  {
    if ( spawnCount > 0 )
    {
      this.createNewAI( "Jim" );

      spawnCount-- ;
    }
    for ( EntityAI ai : this.ais )
    {
      ai.setAiMovement( 1.0f, 0.0f, false );
      ai.setAiRotation( (float) Math.random() * 360, (float) Math.random() * 360 );
      ai.playerTick();
    }
  }
}
