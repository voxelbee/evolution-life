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
    }
  }

  public void stopProccessing( List< UUID > inOrganisms )
  {
    for ( UUID id : inOrganisms )
    {
      organisms.remove( Minecraft.getInstance().world.getPlayerEntityByUUID( id ) );
    }
  }

  public void tick()
  {
    if ( !this.organisms.isEmpty() && tickId % 2 == 0 )
    {
      long timeStart = System.currentTimeMillis();
      List< Float > forwards = new ArrayList< Float >();
      List< Float > strafe = new ArrayList< Float >();
      List< Boolean > jump = new ArrayList< Boolean >();

      for ( EntityOtherPlayerMP organism : organisms )
      {
        this.render.renderView( organism );
        forwards.add( 0.5f );
        strafe.add( 0.0f );
        jump.add( false );
      }
      EvolutionLifePacketHandler.INSTANCE.sendToServer( new FinishedProcessPacket( forwards, strafe, jump ) );
      long timeTotal = System.currentTimeMillis() - timeStart;
      System.out.println( "Time: " + timeTotal );
    }
    tickId++ ;
  }
}
