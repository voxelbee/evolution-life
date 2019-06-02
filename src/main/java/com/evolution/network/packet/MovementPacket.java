package com.evolution.network.packet;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

public class MovementPacket implements AIPacket
{
  public float forward;
  public float strafe;
  public float yaw;
  public float pitch;

  public MovementPacket( float inForawrd, float inStrafe, float inYaw, float inPitch )
  {
    this.forward = inForawrd;
    this.strafe = inStrafe;
    this.yaw = inYaw;
    this.pitch = inPitch;
  }

  public MovementPacket()
  {

  }

  @Override
  public void readPacket( ByteBuf buf )
  {
    this.forward = buf.readFloat();
    this.strafe = buf.readFloat();
    this.yaw = buf.readFloat();
    this.pitch = buf.readFloat();
  }

  @Override
  public void writePacket( ByteBuf buf )
  {
    buf.writeFloat( this.forward );
    buf.writeFloat( this.strafe );
    buf.writeFloat( this.yaw );
    buf.writeFloat( this.pitch );
  }

  @Override
  public void handlePacket( UUID clientId )
  {

  }
}
