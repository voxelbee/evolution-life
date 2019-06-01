package com.evolution;

import com.evolution.network.ServerManager;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class EventListener
{
  @SubscribeEvent
  public void onWorldTick( WorldTickEvent event )
  {
    if ( event.side == LogicalSide.SERVER )
    {
      EvolutionLife.manager.update();
    }
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting( FMLServerStartingEvent event ) throws Exception
  {
    EvolutionLife.manager = new AIManager( event.getServer() );
    EvolutionLife.manager.server = new ServerManager( "localhost", 5000 );
  }
}
