package com.evolution.commands;

import java.util.Collection;

import com.evolution.EvolutionLife;
import com.evolution.server.EntityOrganism;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;

public class StartCommand
{
  public static void register( CommandDispatcher< CommandSource > dispatcher )
  {
    // User types "/start"
    dispatcher.register( Commands.literal( "start" )
        // Needs permission level 2
        .requires( source -> source.hasPermissionLevel( 2 ) )
        // The target players (required argument)
        .then( Commands.argument( "targets", EntityArgument.multiplePlayers() )
            // The item ID (required argument)
            .then( Commands.argument( "count", IntegerArgumentType.integer() )
                // If no further arguments, give one of the item to all targets
                .executes( context -> runStart(
                    context.getSource(),
                    IntegerArgumentType.getInteger( context, "count" ),
                    EntityArgument.getPlayers( context, "targets" ) ) ) ) ) );
  }

  private static int runStart( CommandSource source, int count, Collection< EntityPlayerMP > targets )
      throws CommandSyntaxException
  {
    try
    {
      for ( EntityPlayerMP target : targets )
      {
        if ( !( target instanceof EntityOrganism ) )
        {
          EvolutionLife.serverHandler.addOrganismsToClient( target, count );
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
