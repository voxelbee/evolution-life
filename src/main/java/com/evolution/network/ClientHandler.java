package com.evolution.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.ai.EntityAI;
import com.evolution.network.packet.AIPacket;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.GameType;

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
    return this.networkManager.isConnected;
  }

  public void tick()
  {
    for ( UUID key : this.processingEntities.keySet() )
    {
      EntityAI ai = this.processingEntities.get( key );
      ai.playerTick();
    }
  }

  public void addNewEntity( EntityAI entity )
  {
    EvolutionLife.mcServer.getPlayerList().initializeConnectionToPlayer( new NetworkManager( EnumPacketDirection.CLIENTBOUND ), entity );
    entity.stepHeight = 0.5f;
    entity.setGameType( GameType.SURVIVAL );
    processingEntities.put( entity.getUniqueID(), entity );
  }

  public void removeEntity( UUID id )
  {
    EvolutionLife.mcServer.getPlayerList().playerLoggedOut( processingEntities.get( id ) );
  }
}
