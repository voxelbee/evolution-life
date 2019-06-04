package com.evolution.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.evolution.EvolutionLife;
import com.evolution.network.AINetworkManager;
import com.evolution.network.ClientHandler;
import com.evolution.network.packet.PacketConnectionSuccess;
import com.evolution.network.packet.PacketDispatchEntity;
import com.evolution.network.packet.PacketRequestEntities;
import com.evolution.network.packet.PacketSocketConnect;

public class EvolutionManager
{
  private DNAGenerator breeder;

  private Map< UUID, ClientHandler > clients = new HashMap< UUID, ClientHandler >();
  private BlockingQueue< EntityAI > entitiesToSpawn = new LinkedBlockingQueue< EntityAI >();

  public int counter = 0;

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
    this.checkClientConnections();
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
    int countAdded = 0;
    for ( int i = 0; i < packet.numberOfEntities; i++ )
    {
      EntityAI entity = entitiesToSpawn.poll();
      if ( entity != null )
      {
        entity.setOwner( clientID );
        this.getClientHandler( clientID ).addNewEntity( entity );
        this.getClientHandler( clientID ).sendPacket( new PacketDispatchEntity( clientID, entity.getDNA() ) );
        System.out.println( "Spawned existing..." );
      }
      else
      {
        countAdded = i;
        break;
      }
    }

    int count = packet.numberOfEntities - countAdded;
    for ( int i = 0; i < count; i++ )
    {
      UUID newID = UUID.randomUUID();
      EntityAI entity = new EntityAI( newID, newID.toString().substring( 0, 6 ), clientID );
      this.getClientHandler( clientID ).addNewEntity( entity );
      this.breeder.createNewStrand( entity.getUniqueID() );
    }
  }

  public ClientHandler getClientHandler( UUID id )
  {
    return this.clients.get( id );
  }

  private void checkClientConnections()
  {
    // this.clients.values().removeIf( entry -> !entry.isConnected() );
    for ( Iterator< UUID > iterator = this.clients.keySet().iterator(); iterator.hasNext(); )
    {
      UUID key = iterator.next();
      if ( !this.clients.get( key ).isConnected() )
      {
        ClientHandler handler = this.clients.get( key );
        this.handleSocketDisconnect( handler );
        handler.close();
        iterator.remove();
      }
    }
  }

  public void handleSocketDisconnect( ClientHandler clientHandler )
  {
    for ( EntityAI entityAI : clientHandler.getAllEntites() )
    {
      try
      {
        this.entitiesToSpawn.put( entityAI );
      }
      catch ( InterruptedException e )
      {
        e.printStackTrace();
      }
    }
    clientHandler.removeAllEntites();
  }
}
