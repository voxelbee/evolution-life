package com.evolution.server;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEditBook;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketNBTQueryEntity;
import net.minecraft.network.play.client.CPacketNBTQueryTileEntity;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.network.play.client.CPacketUpdateCommandMinecart;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

public class PlayServerWrapper extends NetHandlerPlayServer
{
  private final MinecraftServer server;
  public EntityOrganism player;

  public PlayServerWrapper( MinecraftServer server, EntityOrganism playerIn )
  {
    super( server, new DummyNetworkManager(), playerIn );
    this.server = server;
    this.player = playerIn;
    playerIn.connection = this;
  }

  @Override
  public void tick()
  {

  }

  @Override
  public void captureCurrentPosition()
  {

  }

  /**
   * Disconnect the player with a specified reason
   */
  @Override
  public void disconnect( ITextComponent textComponent )
  {

  }

  /**
   * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
   * flying/sprinting
   */
  @Override
  public void processInput( CPacketInput packetIn )
  {

  }

  @Override
  public void processVehicleMove( CPacketVehicleMove packetIn )
  {

  }

  @Override
  public void processConfirmTeleport( CPacketConfirmTeleport packetIn )
  {

  }

  @Override
  public void handleRecipeBookUpdate( CPacketRecipeInfo packetIn )
  {

  }

  @Override
  public void handleSeenAdvancements( CPacketSeenAdvancements packetIn )
  {

  }

  /**
   * This method is only called for manual tab-completion (the {@link
   * net.minecraft.command.arguments.SuggestionProviders#ASK_SERVER minecraft:ask_server} suggestion provider).
   */
  @Override
  public void processTabComplete( CPacketTabComplete packetIn )
  {

  }

  @Override
  public void processUpdateCommandBlock( CPacketUpdateCommandBlock packetIn )
  {

  }

  @Override
  public void processUpdateCommandMinecart( CPacketUpdateCommandMinecart packetIn )
  {

  }

  @Override
  public void processPickItem( CPacketPickItem packetIn )
  {

  }

  @Override
  public void processRenameItem( CPacketRenameItem packetIn )
  {

  }

  @Override
  public void processUpdateBeacon( CPacketUpdateBeacon packetIn )
  {

  }

  @Override
  public void processUpdateStructureBlock( CPacketUpdateStructureBlock packetIn )
  {

  }

  @Override
  public void processSelectTrade( CPacketSelectTrade packetIn )
  {

  }

  @Override
  public void processEditBook( CPacketEditBook packetIn )
  {

  }

  @Override
  public void processNBTQueryEntity( CPacketNBTQueryEntity packetIn )
  {

  }

  @Override
  public void processNBTQueryBlockEntity( CPacketNBTQueryTileEntity packetIn )
  {

  }

  /**
   * Processes clients perspective on player positioning and/or orientation
   */
  @Override
  public void processPlayer( CPacketPlayer packetIn )
  {

  }

  @Override
  public void setPlayerLocation( double x, double y, double z, float yaw, float pitch )
  {
    this.setPlayerLocation( x, y, z, yaw, pitch, Collections.emptySet() );
  }

  /**
   * Teleports the player position to the (relative) values specified, and syncs to the client
   */
  @Override
  public void setPlayerLocation( double x, double y, double z, float yaw, float pitch, Set< SPacketPlayerPosLook.EnumFlags > relativeSet )
  {
    this.player.setPositionAndRotation( x, y, z, yaw, pitch );
  }

  /**
   * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items
   */
  @Override
  public void processPlayerDigging( CPacketPlayerDigging packetIn )
  {

  }

  @Override
  public void processTryUseItemOnBlock( CPacketPlayerTryUseItemOnBlock packetIn )
  {

  }

  /**
   * Called when a client is using an item while not pointing at a block, but simply using an item
   */
  @Override
  public void processTryUseItem( CPacketPlayerTryUseItem packetIn )
  {

  }

  @Override
  public void handleSpectate( CPacketSpectate packetIn )
  {

  }

  @Override
  public void handleResourcePackStatus( CPacketResourcePackStatus packetIn )
  {
  }

  @Override
  public void processSteerBoat( CPacketSteerBoat packetIn )
  {

  }

  /**
   * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
   */
  @Override
  public void onDisconnect( ITextComponent reason )
  {
    this.server.refreshStatusNextTick();
    this.player.disconnect();
  }

  @Override
  public void sendPacket( Packet< ? > packetIn )
  {
    this.sendPacket( packetIn, (GenericFutureListener< ? extends Future< ? super Void > >) null );
  }

  @Override
  public void sendPacket( Packet< ? > packetIn, @Nullable GenericFutureListener< ? extends Future< ? super Void > > futureListeners )
  {
    if ( packetIn instanceof SPacketChunkData )
    {
      SPacketChunkData data = (SPacketChunkData) packetIn;
      data.getChunkX();
    }
    // System.out.println( "Sending packet: " + packetIn.getClass().getSimpleName() );
  }

  /**
   * Updates which quickbar slot is selected
   */
  @Override
  public void processHeldItemChange( CPacketHeldItemChange packetIn )
  {

  }

  /**
   * Process chat messages (broadcast back to clients) and commands (executes)
   */
  @Override
  public void processChatMessage( CPacketChatMessage packetIn )
  {

  }

  @Override
  public void handleAnimation( CPacketAnimation packetIn )
  {

  }

  /**
   * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
   * height of the horse the player is riding
   */
  @Override
  public void processEntityAction( CPacketEntityAction packetIn )
  {

  }

  /**
   * Processes left and right clicks on entities
   */
  @Override
  public void processUseEntity( CPacketUseEntity packetIn )
  {

  }

  /**
   * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or acquiring
   * 'open inventory' achievement
   */
  @Override
  public void processClientStatus( CPacketClientStatus packetIn )
  {

  }

  /**
   * Processes the client closing windows (container)
   */
  @Override
  public void processCloseWindow( CPacketCloseWindow packetIn )
  {

  }

  /**
   * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
   * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
   * the same open container/inventory
   */
  @Override
  public void processClickWindow( CPacketClickWindow packetIn )
  {

  }

  @Override
  public void processPlaceRecipe( CPacketPlaceRecipe packetIn )
  {

  }

  /**
   * Enchants the item identified by the packet given some convoluted conditions (matching window, which
   * should/shouldn't be in use?)
   */
  @Override
  public void processEnchantItem( CPacketEnchantItem packetIn )
  {

  }

  /**
   * Update the server with an ItemStack in a slot.
   */
  @Override
  public void processCreativeInventoryAction( CPacketCreativeInventoryAction packetIn )
  {

  }

  /**
   * Received in response to the server requesting to confirm that the client-side open container matches the servers'
   * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
   * contents
   */
  @Override
  public void processConfirmTransaction( CPacketConfirmTransaction packetIn )
  {

  }

  @Override
  public void processUpdateSign( CPacketUpdateSign packetIn )
  {

  }

  /**
   * Updates a players' ping statistics
   */
  @Override
  public void processKeepAlive( CPacketKeepAlive packetIn )
  {

  }

  /**
   * Processes a player starting/stopping flying
   */
  @Override
  public void processPlayerAbilities( CPacketPlayerAbilities packetIn )
  {

  }

  /**
   * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
   * and whether to show the cape
   */
  @Override
  public void processClientSettings( CPacketClientSettings packetIn )
  {

  }

  /**
   * Synchronizes serverside and clientside book contents and signing
   */
  @Override
  public void processCustomPayload( CPacketCustomPayload packetIn )
  {

  }
}
