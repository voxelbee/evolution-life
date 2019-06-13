package com.evolution.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.evolution.tensorflow.Network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;

public class ClientHandler
{
  private int renderSize = 32;

  private ViewRender render;

  private int tickId;

  private Map< UUID, Network > networks = new HashMap< UUID, Network >();

  public ClientHandler()
  {
    this.render = new ViewRender( renderSize, renderSize );
  }

  public void requestProcess( UUID inOrganism, byte[] inDna )
  {
    this.networks.put( inOrganism, new Network( inOrganism, renderSize, 5, inDna ) );
  }

  public void stopProccessing( Set< UUID > inOrganisms )
  {
    for ( UUID id : inOrganisms )
    {
      Network net = this.networks.remove( id );
      net.stop();
    }
  }

  public void tick()
  {
    if ( !this.networks.isEmpty() && tickId % 2 == 0 )
    {
      for ( UUID id : this.networks.keySet() )
      {
        EntityOtherPlayerMP organism = (EntityOtherPlayerMP) Minecraft.getInstance().world.getPlayerEntityByUUID( id );
        this.render.renderView( organism );
        NativeImage image = ScreenShotHelper.createScreenshot( renderSize, renderSize, this.render.getFrameBuffer() );
        this.networks.get( id ).addTask( image );
      }
    }
    tickId++ ;
  }
}
