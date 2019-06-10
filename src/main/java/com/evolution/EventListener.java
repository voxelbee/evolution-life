package com.evolution;

import java.io.IOException;

import com.evolution.client.ClientHandler;
import com.evolution.commands.StartCommand;
import com.evolution.commands.StopCommand;
import com.evolution.server.EntityOrganism;
import com.evolution.server.ServerHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

public class EventListener
{
  private boolean first = true;

  @SubscribeEvent
  public void onServerTick( ServerTickEvent event )
  {
    EvolutionLife.serverHandler.tick();
  }

  @SubscribeEvent
  public void onClientTick( ClientTickEvent e ) throws IOException
  {
    if ( Minecraft.getInstance().world != null )
    {
      if ( first )
      {
        EvolutionLife.clientHandler = new ClientHandler();
        this.first = false;
      }
      if ( !Minecraft.getInstance().isGamePaused() )
      {
        EvolutionLife.clientHandler.tick();
      }
    }
  }

  @SubscribeEvent
  public void onServerStarting( FMLServerStartingEvent event )
  {
    StartCommand.register( event.getCommandDispatcher() );
    StopCommand.register( event.getCommandDispatcher() );

    EvolutionLife.mcServer = event.getServer();
    EvolutionLife.serverHandler = new ServerHandler();
  }

  @SubscribeEvent
  public void onServerStopping( FMLServerStoppingEvent event )
  {
    EvolutionLife.serverHandler.close();
  }

  @SubscribeEvent
  public void onClientJoin( PlayerLoggedInEvent event )
  {
    EvolutionLife.serverHandler.addClient( (EntityPlayerMP) event.getPlayer() );
  }

  @SubscribeEvent
  public void onClientLeave( PlayerLoggedOutEvent event )
  {
    if ( !( event.getPlayer() instanceof EntityOrganism ) )
    {
      EvolutionLife.serverHandler.removeClient( (EntityPlayerMP) event.getPlayer(), false );
    }
  }
}
