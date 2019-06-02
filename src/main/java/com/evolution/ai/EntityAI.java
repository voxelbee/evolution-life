package com.evolution.ai;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;

public class EntityAI extends EntityPlayerMP
{
  public UUID owner;

  public EntityAI( MinecraftServer server, WorldServer worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn )
  {
    super( server, worldIn, profile, interactionManagerIn );
  }

  public void setAiMovement( float forward, float strafe )
  {
    this.moveForward = MathHelper.clamp( forward, -0.3f, 0.5f );
    this.moveStrafing = MathHelper.clamp( strafe, -0.3f, 0.3f );
  }

  public void setAiRotation( float yaw, float pitch )
  {
    this.rotationPitch = pitch % 360;
    this.rotationYaw = yaw % 360;
  }
}
