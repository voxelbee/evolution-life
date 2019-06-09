package com.evolution.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;

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
    for ( EntityPlayerMP client : clients.keySet() )
    {
      for ( EntityOrganism organsim : clients.get( client ) )
      {
        organsim.playerTick();
      }
    }
  }

  public void addClient( EntityPlayerMP client )
  {
    List< EntityOrganism > ais = new ArrayList< EntityOrganism >();
    this.clients.put( client, ais );
  }

  public void removeClient( EntityPlayerMP client )
  {
    if ( this.clients.size() != 1 )
    {
      List< EntityOrganism > ais = this.clients.remove( client );
      for ( EntityOrganism entityAI : ais )
      {
        entityAI.removeFromWorld();
      }
    }
    else
    {
      this.clients.remove( client );
    }
  }

  public void addOrgansimsToClient( EntityPlayerMP client, int count )
  {
    List< EntityOrganism > ais = this.clients.get( client );
    for ( int i = 0; i < count; i++ )
    {
      ais.add( new EntityOrganism( UUID.randomUUID(), "Jim" ) );
      ais.get( i ).addToWorld();
    }
  }
}
