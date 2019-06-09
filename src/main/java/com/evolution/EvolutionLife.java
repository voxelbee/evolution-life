package com.evolution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evolution.client.ClientHandler;
import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.OrganismCountPacket;
import com.evolution.server.ServerHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod( "evolution-life" )
public class EvolutionLife
{
  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  public static final String MODID = "evolution-life";
  public static final String MODNAME = "Evolution";
  public static final String MODVERSION = "0.0.1";

  public static ServerHandler serverHandler;
  public static ClientHandler clientHandler;

  public static MinecraftServer mcServer;

  public EvolutionLife()
  {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener( this::setup );
    // Register the enqueueIMC method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener( this::enqueueIMC );
    // Register the processIMC method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener( this::processIMC );
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener( this::doClientStuff );

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register( this );

    int id = 0;
    EvolutionLifePacketHandler.INSTANCE.registerMessage( id++ ,
        OrganismCountPacket.class,
        OrganismCountPacket.ENCODER,
        OrganismCountPacket.DECODER,
        EvolutionLifePacketHandler::handleCountPacket );
  }

  private void setup( final FMLCommonSetupEvent event )
  {
    MinecraftForge.EVENT_BUS.register( new EventListener() );
  }

  private void doClientStuff( final FMLClientSetupEvent event )
  {
    // do something that can only be done on the client
    LOGGER.info( "Got game settings {}", event.getMinecraftSupplier().get().gameSettings );
  }

  private void enqueueIMC( final InterModEnqueueEvent event )
  {

  }

  private void processIMC( final InterModProcessEvent event )
  {

  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the
  // MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
  public static class RegistryEvents
  {

  }
}
