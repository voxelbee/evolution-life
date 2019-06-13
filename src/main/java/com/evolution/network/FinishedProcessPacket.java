package com.evolution.network;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public class FinishedProcessPacket
{
  public UUID organism;

  public float forwards;
  public float strafe;
  public float yaw;
  public float pitch;

  public boolean jump;

  public FinishedProcessPacket()
  {

  }

  public FinishedProcessPacket( UUID inOrganism, float inForwards, float inStrafe, float inYaw, float inPitch, boolean inJump )
  {
    this.organism = inOrganism;
    this.forwards = inForwards;
    this.strafe = inStrafe;
    this.yaw = inYaw;
    this.pitch = inPitch;
    this.jump = inJump;
  }

  static public final BiConsumer< FinishedProcessPacket, PacketBuffer > ENCODER = ( msg, buffer ) ->
  {
    buffer.writeUniqueId( msg.organism );
    buffer.writeFloat( msg.forwards );
    buffer.writeFloat( msg.strafe );
    buffer.writeShort( (short) ( msg.yaw * 90 ) );
    buffer.writeShort( (short) ( msg.pitch * 90 ) );
    buffer.writeBoolean( msg.jump );
  };

  static public final Function< PacketBuffer, FinishedProcessPacket > DECODER = ( buffer ) ->
  {
    UUID id = buffer.readUniqueId();
    float forwards = buffer.readFloat();
    float strafe = buffer.readFloat();
    float yaw = buffer.readShort() / 90.0f;
    float pitch = buffer.readShort() / 90.0f;
    boolean jump = buffer.readBoolean();
    return new FinishedProcessPacket( id, forwards, strafe, yaw, pitch, jump );
  };
}
