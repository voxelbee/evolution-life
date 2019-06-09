package com.evolution.ai;

import java.util.UUID;

import com.evolution.EvolutionLife;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

public class EntityAI extends EntityPlayerMP
{
  private UUID owner;
  private byte[] dna;

  public EntityAI( UUID entityID, String name, UUID owner )
  {
    super( EvolutionLife.mcServer,
        EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ),
        new GameProfile( entityID, name ),
        new PlayerInteractionManager( EvolutionLife.mcServer.getWorld( DimensionType.OVERWORLD ) ) );
    this.stepHeight = 0.5f;
    this.owner = owner;
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

  public byte[] getDNA()
  {
    return this.dna;
  }

  public void setDNA( byte[] inDna )
  {
    this.dna = inDna;
  }

  public UUID getOwner()
  {
    return this.owner;
  }

  public void setOwner( UUID id )
  {
    this.owner = id;
  }
}
