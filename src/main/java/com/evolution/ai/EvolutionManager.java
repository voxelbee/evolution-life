package com.evolution.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import com.evolution.EvolutionLife;
import com.evolution.network.AINetworkManager;
import com.evolution.network.ClientHandler;
import com.evolution.network.packet.PacketConnectionSuccess;
import com.evolution.network.packet.PacketDispatchEntities;
import com.evolution.network.packet.PacketRequestEntities;
import com.evolution.network.packet.PacketSocketConnect;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.dimension.DimensionType;

public class EvolutionManager
{
  private DNAGenerator breeder;

  private Map< UUID, ClientHandler > clients = new HashMap< UUID, ClientHandler >();
  private Queue< EntityAI > entitiesToSpawn = new PriorityQueue< EntityAI >();

  public EvolutionManager()
  {
    AINetworkManager.createServer( EvolutionLife.ADDRESS, EvolutionLife.PORT );

    this.breeder = new DNAGenerator();
    Thread threadBreeder = new Thread( this.breeder );
    threadBreeder.setName( "DNA Generator" );
    threadBreeder.start();
  }

  public void close()
  {
    this.breeder.stop();
  }

  public void tick()
  {
    for ( UUID key : this.clients.keySet() )
    {
      this.clients.get( key ).tick();
    }
  }

  public void handleSocketConnect( PacketSocketConnect packet )
  {
    UUID id = UUID.randomUUID();
    packet.netManager.setClientID( id );
    clients.put( id, new ClientHandler( packet.netManager ) );
    clients.get( id ).sendPacket( new PacketConnectionSuccess() );
    System.out.println( "Client connected..." );
  }

  public void handleClientEntityRequest( PacketRequestEntities packet, UUID clientID )
  {
    List< UUID > entityIds = new ArrayList< UUID >();
    for ( int i = 0; i < packet.numberOfEntities; i++ )
    {
      EntityAI entity = entitiesToSpawn.poll();
      if ( entity != null )
      {
        this.getClientHandler( clientID ).addNewEntity( entity );
        entityIds.add( entity.getUniqueID() );
      }
      else
      {
        break;
      }
    }

    int count = packet.numberOfEntities - entityIds.size();
    for ( int i = 0; i < count; i++ )
    {
      EntityAI entity = this.createNewAI();
      this.getClientHandler( clientID ).addNewEntity( entity );
      entityIds.add( entity.getUniqueID() );
    }

    this.getClientHandler( clientID ).sendPacket( new PacketDispatchEntities( entityIds ) );
  }

  public EntityAI createNewAI()
  {
    EntityAI entity = new EntityAI( EvolutionLife.mcServer,
        EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( UUID.randomUUID(), "Jim" ),
        new PlayerInteractionManager( EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ) ) );
    return entity;
  }

  public ClientHandler getClientHandler( UUID id )
  {
    return this.clients.get( id );
  }
}
