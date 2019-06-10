package com.evolution.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.FinishedProcessPacket;
import com.evolution.network.RequestProcessPacket;
import com.evolution.network.StopProcessPacket;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerHandler
{
  private Map< EntityPlayerMP, List< EntityOrganism > > clients = new HashMap< EntityPlayerMP, List< EntityOrganism > >();

  public ServerHandler()
  {

  }

  public void close()
  {

  }

  public void tick()
  {

  }

  public void clientProcessResponse( FinishedProcessPacket response, EntityPlayerMP client )
  {
    List< EntityOrganism > organisms = this.clients.get( client );
    for ( int i = 0; i < organisms.size(); i++ )
    {
      organisms.get( i ).setAiMovement( response.forwards.get( i ), response.strafe.get( i ) );
      organisms.get( i ).playerTick();
    }
  }

  public void addClient( EntityPlayerMP client )
  {
    this.clients.put( client, new ArrayList< EntityOrganism >() );
  }

  public void removeClient( EntityPlayerMP client, boolean fromCommand )
  {
    if ( fromCommand )
    {
      List< EntityOrganism > organisms = this.clients.get( client );
      for ( EntityOrganism entityAI : organisms )
      {
        entityAI.removeFromWorld();
      }
      organisms.clear();
    }
    else if ( this.clients.size() != 1 )
    {
      List< EntityOrganism > organisms = this.clients.remove( client );
      for ( EntityOrganism entityAI : organisms )
      {
        entityAI.removeFromWorld();
      }
    }
    else
    {
      this.clients.remove( client );
    }
  }

  public void sendRemoveClients( EntityPlayerMP client )
  {
    List< UUID > ids = new ArrayList< UUID >();
    for ( EntityOrganism organism : this.clients.get( client ) )
    {
      ids.add( organism.getUniqueID() );
    }
    EvolutionLifePacketHandler.INSTANCE.send( PacketDistributor.PLAYER.with( () -> client ), new StopProcessPacket( ids ) );
  }

  public void addOrgansimsToClient( EntityPlayerMP client, int count )
  {
    List< EntityOrganism > orgamisms = this.clients.get( client );
    List< UUID > ids = new ArrayList< UUID >();
    for ( int i = 0; i < count; i++ )
    {
      orgamisms.add( new EntityOrganism( UUID.randomUUID(), "Jim" ) );
      orgamisms.get( i ).addToWorld();
      ids.add( orgamisms.get( i ).getUniqueID() );
    }
    EvolutionLifePacketHandler.INSTANCE.send( PacketDistributor.PLAYER.with( () -> client ), new RequestProcessPacket( ids ) );
  }
}
