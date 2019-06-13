package com.evolution.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.FinishedProcessPacket;
import com.evolution.network.StopProcessPacket;
import com.evolution.server.DNAGenerator.EnumTaskType;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerHandler
{
  private Map< EntityPlayerMP, Map< UUID, EntityOrganism > > clients = new HashMap< EntityPlayerMP, Map< UUID, EntityOrganism > >();

  private DNAGenerator breeder;

  public ServerHandler()
  {
    this.breeder = new DNAGenerator( 100 );
  }

  public void close()
  {

  }

  public void tick()
  {

  }

  /**
   * Handles incoming packets from clients that have processed there organism.
   *
   * @param response
   * @param client
   */
  public void clientProcessResponse( FinishedProcessPacket response, EntityPlayerMP client )
  {
    EntityOrganism organism = this.clients.get( client ).get( response.organism );
    organism.setAiMovement( response.forwards, response.strafe );
    organism.setAiRotation( response.yaw, response.pitch );
    organism.setJumping( response.jump );
    organism.playerTick();
  }

  /**
   * Adds a new client to start processing.
   *
   * @param client
   */
  public void addClient( EntityPlayerMP client )
  {
    this.clients.put( client, new HashMap< UUID, EntityOrganism >() );
  }

  /**
   * Removes a client from the calculator. Removing all of the organisms from the
   * client too by sending a stop packet.
   *
   * @param client
   */
  public void removeClient( EntityPlayerMP client )
  {
    this.removeOrganismsFromClient( client, this.clients.get( client ).keySet() );
    Map< UUID, EntityOrganism > organisms = this.clients.get( client );
    for ( EntityOrganism entityAI : organisms.values() )
    {
      entityAI.removeFromWorld();
    }
    organisms.clear();
  }

  /**
   * Sends to the client to remove the organisms specified in the set.
   *
   * @param client
   */
  public void removeOrganismsFromClient( EntityPlayerMP client, Set< UUID > organisms )
  {
    EvolutionLifePacketHandler.INSTANCE.send( PacketDistributor.PLAYER.with( () -> client ), new StopProcessPacket( organisms ) );
  }

  /**
   * Adds the specified count of organisms to the client sending the ids
   * to the client too. This also starts the client processing of the
   * organism.
   *
   * @param client
   * @param count
   */
  public void addOrganismsToClient( EntityPlayerMP client, int count )
  {
    Map< UUID, EntityOrganism > orgamisms = this.clients.get( client );
    for ( int i = 0; i < count; i++ )
    {
      UUID id = UUID.randomUUID();
      orgamisms.put( id, new EntityOrganism( id, "Jim" ) );
      orgamisms.get( id ).addToWorld();
      this.breeder.addTask( EnumTaskType.RANDOM, client, id );
    }
  }
}
