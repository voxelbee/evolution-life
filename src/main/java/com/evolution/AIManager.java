package com.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
  private int numberToSpawn = 100;

  public void createNewAI( MinecraftServer server )
  {
    EntityAI player = new EntityAI( server,
        server.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( UUID.randomUUID(), "Jim" + numberToSpawn ),
        new PlayerInteractionManager( server.getWorld( DimensionType.OVERWORLD ) ) );
    server.getPlayerList().initializeConnectionToPlayer( new NetworkManager( EnumPacketDirection.CLIENTBOUND ), player );
    player.stepHeight = 0.5f;
    player.setGameType( GameType.SURVIVAL );
    ais.add( player );
  }

  public void update( MinecraftServer server )
  {
    for ( EntityAI ai : ais )
    {
      ai.setMovement( 1.0f, 0.0f, false );
      ai.setRotation( (float) Math.random() * 360, (float) Math.random() * 360 );
      ai.playerTick();
    }
  }
}
