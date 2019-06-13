package com.evolution.commands;

import java.util.Collection;

import com.evolution.EvolutionLife;
import com.evolution.server.EntityOrganism;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;

public class StopCommand
{
  public static void register( CommandDispatcher< CommandSource > dispatcher )
  {
    // User types "/stop"
    dispatcher.register( Commands.literal( "stop" )
        // Needs permission level 2
        .requires( source -> source.hasPermissionLevel( 2 ) )
        // The target players (required argument)
        .then( Commands.argument( "targets", EntityArgument.multiplePlayers() )
            .executes( context -> runStop(
                context.getSource(),
                EntityArgument.getPlayers( context, "targets" ) ) ) ) );
  }

  private static int runStop( CommandSource source, Collection< EntityPlayerMP > targets )
      throws CommandSyntaxException
  {
    try
    {
      for ( EntityPlayerMP target : targets )
      {
        if ( !( target instanceof EntityOrganism ) )
        {
          EvolutionLife.serverHandler.removeClient( target );
        }
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return 1;
  }
}
