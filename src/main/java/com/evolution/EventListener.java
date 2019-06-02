package com.evolution;

import com.evolution.ai.EvolutionManager;
import com.evolution.network.MainThreadPacketHandler;
import com.evolution.network.ServerManager;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class EventListener
{
  @SubscribeEvent
  public void onServerTick( ServerTickEvent event )
  {
    MainThreadPacketHandler.tick();
    EvolutionLife.manager.tick();
  }

  @SubscribeEvent
  public void onServerStarting( FMLServerStartingEvent event ) throws Exception
  {
    // Creates the evolution manager
    EvolutionLife.manager = new EvolutionManager( event.getServer(), new ServerManager( EvolutionLife.ADDRESS, EvolutionLife.PORT ) );
  }
}
