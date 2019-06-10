package com.evolution;

import java.util.Collection;

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
    // User types "/start"
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
    for ( EntityPlayerMP target : targets )
    {
      EvolutionLife.serverHandler.sendRemoveClients( target );
      EvolutionLife.serverHandler.removeClient( target, true );
    }
    return 1;
  }
}
