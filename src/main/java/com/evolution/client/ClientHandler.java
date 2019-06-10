package com.evolution.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.FinishedProcessPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class ClientHandler
{
  private int renderSize = 32;

  private ViewRender render;

  private int tickId;

  private List< EntityOtherPlayerMP > organisms = new ArrayList< EntityOtherPlayerMP >();

  private List< Float > forwards = new ArrayList< Float >();
  private List< Float > strafe = new ArrayList< Float >();
  private List< Boolean > jump = new ArrayList< Boolean >();

  public ClientHandler()
  {
    this.render = new ViewRender( renderSize, renderSize );
  }

  public void requestProcess( List< UUID > inOrganisms )
  {
    this.organisms.clear();
    for ( UUID id : inOrganisms )
    {
      this.organisms.add( (EntityOtherPlayerMP) Minecraft.getInstance().world.getPlayerEntityByUUID( id ) );
      this.forwards.add( 0.0f );
      this.strafe.add( 0.0f );
      this.jump.add( false );
    }
  }

  public void stopProccessing( List< UUID > inOrganisms )
  {
    for ( UUID id : inOrganisms )
    {
      int i = this.organisms.indexOf( Minecraft.getInstance().world.getPlayerEntityByUUID( id ) );
      this.organisms.remove( i );
      this.forwards.remove( i );
      this.strafe.remove( i );
      this.jump.remove( i );
    }
  }

  public void tick()
  {
    if ( !this.organisms.isEmpty() && tickId % 2 == 0 )
    {
      int i = 0;
      for ( EntityOtherPlayerMP organism : organisms )
      {
        this.render.renderView( organism );
        this.forwards.set( i, 0.1f );
        this.strafe.set( i, 0.0f );
        this.jump.set( i, false );
        i++ ;
      }
      EvolutionLifePacketHandler.INSTANCE.sendToServer( new FinishedProcessPacket( forwards, strafe, jump ) );
    }
    tickId++ ;
  }
}
