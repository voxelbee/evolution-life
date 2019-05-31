package com.evolution;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
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

  public static AIManager manager;

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
  }

  private void setup( final FMLCommonSetupEvent event )
  {
    // some preinit code
    LOGGER.info( "HELLO FROM PREINIT" );
    LOGGER.info( "DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName() );

    MinecraftForge.EVENT_BUS.register( new EventListener() );
  }

  private void doClientStuff( final FMLClientSetupEvent event )
  {
    // do something that can only be done on the client
    LOGGER.info( "Got game settings {}", event.getMinecraftSupplier().get().gameSettings );
  }

  private void enqueueIMC( final InterModEnqueueEvent event )
  {
    // some example code to dispatch IMC to another mod
    InterModComms.sendTo( "evolution-life", "helloworld", () ->
    {
      LOGGER.info( "Hello world from the MDK" );
      return "Hello world";
    } );
  }

  private void processIMC( final InterModProcessEvent event )
  {
    // some example code to receive and process InterModComms from other mods
    LOGGER.info( "Got IMC {}", event.getIMCStream().map( m -> m.getMessageSupplier().get() ).collect( Collectors.toList() ) );
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting( FMLServerStartingEvent event ) throws Exception
  {
    manager = new AIManager( event.getServer() );
    AIServer server = new AIServer( "localhost", 5000 );
    Thread serverThread = new Thread( server );
    serverThread.start();
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the
  // MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
  public static class RegistryEvents
  {

  }
}
