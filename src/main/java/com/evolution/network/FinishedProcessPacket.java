package com.evolution.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public class FinishedProcessPacket
{
  public List< Float > forwards;
  public List< Float > strafe;

  public List< Boolean > jump;

  public FinishedProcessPacket()
  {

  }

  public FinishedProcessPacket( List< Float > inForwards, List< Float > inStrafe, List< Boolean > inJump )
  {
    this.forwards = inForwards;
    this.strafe = inStrafe;
    this.jump = inJump;
  }

  static public final BiConsumer< FinishedProcessPacket, PacketBuffer > ENCODER = ( msg, buffer ) ->
  {
    buffer.writeVarInt( msg.forwards.size() );
    for ( int i = 0; i < msg.forwards.size(); i++ )
    {
      buffer.writeFloat( msg.forwards.get( i ) );
      buffer.writeFloat( msg.strafe.get( i ) );
      buffer.writeBoolean( msg.jump.get( i ) );
    }
  };

  static public final Function< PacketBuffer, FinishedProcessPacket > DECODER = ( buffer ) ->
  {
    List< Float > forwards = new ArrayList< Float >();
    List< Float > strafe = new ArrayList< Float >();
    List< Boolean > jump = new ArrayList< Boolean >();

    int count = buffer.readVarInt();
    for ( int i = 0; i < count; i++ )
    {
      forwards.add( buffer.readFloat() );
      strafe.add( buffer.readFloat() );
      jump.add( buffer.readBoolean() );
    }
    return new FinishedProcessPacket( forwards, strafe, jump );
  };
}
