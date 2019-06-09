package com.evolution.network;

import java.util.function.Supplier;

import com.evolution.EvolutionLife;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class EvolutionLifePacketHandler
{
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
      new ResourceLocation( EvolutionLife.MODID, "main" ),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals );

  public static void handleCountPacket( OrganismCountPacket msg, Supplier< NetworkEvent.Context > ctx )
  {
    ctx.get().enqueueWork( () ->
    {
      if ( isServer( ctx.get() ) )
      {
        EvolutionLife.serverHandler.addOrgansimsToClient( ctx.get().getSender(), msg.numberOfOrganisms );
      }
    } );
    ctx.get().setPacketHandled( true );
  }

  private static boolean isServer( NetworkEvent.Context ctx )
  {
    return ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER || ctx.getDirection() == NetworkDirection.LOGIN_TO_SERVER;
  }
}
