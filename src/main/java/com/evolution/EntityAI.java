package com.evolution;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;

public class EntityAI extends EntityPlayerMP
{
  protected EntityAI( MinecraftServer server, WorldServer worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn )
  {
    super( server, worldIn, profile, interactionManagerIn );
  }

  public void setMovement( float forward, float strafe, boolean sprint )
  {
    this.setSprinting( sprint );
    this.moveForward = MathHelper.clamp( forward, -0.3f, 0.5f );
    this.moveStrafing = MathHelper.clamp( strafe, -0.3f, 0.3f );
  }

  @Override
  public void setRotation( float yaw, float pitch )
  {
    this.rotationPitch = pitch % 360;
    this.rotationYaw = yaw % 360;
  }
}
