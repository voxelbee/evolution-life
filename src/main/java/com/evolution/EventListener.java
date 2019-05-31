package com.evolution;

import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EventListener
{
  int count = 0;

  @SubscribeEvent
  public void onCommand( CommandEvent event )
  {

  }

  @SubscribeEvent
  public void onWorldTick( WorldTickEvent event )
  {
    if ( event.side == LogicalSide.CLIENT )
    {
      return;
    }
    else
    {
      EvolutionLife.manager.update( event.world.getServer() );
    }
  }
}
