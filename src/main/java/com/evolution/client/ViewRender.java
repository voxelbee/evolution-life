package com.evolution.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

public class ViewRender
{
  private Framebuffer buffer;
  private FogRenderer fogRender;
  private LightTexture lightMap;
  private float farPlaneDistance;
  private double fov;
  private int frameCount;
  private final float[] rainXCoords = new float[ 1024 ];
  private final float[] rainYCoords = new float[ 1024 ];
  private final Random random = new Random();
  private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation( "textures/environment/rain.png" );
  private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation( "textures/environment/snow.png" );

  public ViewRender( int width, int height )
  {
    this.buffer = new Framebuffer( width, height, true );
    this.fogRender = new FogRenderer( Minecraft.getInstance().entityRenderer );
    this.lightMap = new LightTexture( Minecraft.getInstance().entityRenderer );
    this.fov = 70D;

    for ( int i = 0; i < 32; ++i )
    {
      for ( int j = 0; j < 32; ++j )
      {
        float f = j - 16;
        float f1 = i - 16;
        float f2 = MathHelper.sqrt( f * f + f1 * f1 );
        this.rainXCoords[ i << 5 | j ] = -f1 / f2;
        this.rainYCoords[ i << 5 | j ] = f / f2;
      }
    }
  }

  public void renderView( Entity view )
  {
    Minecraft.getInstance().setRenderViewEntity( view );
    GlStateManager.pushMatrix();
    GlStateManager.clear( 16640 );
    this.buffer.bindFramebuffer( true );
    GlStateManager.enableTexture2D();

    GlStateManager.enableDepthTest();
    GlStateManager.enableAlphaTest();
    GlStateManager.alphaFunc( 516, 0.5F );
    this.render( 1.0f, 1 );

    GlStateManager.popMatrix();
    this.buffer.unbindFramebuffer();
    Minecraft.getInstance().setRenderViewEntity( Minecraft.getInstance().player );
  }

  private void render( float partialTicks, long finishTimeNano )
  {
    WorldRenderer worldrenderer = Minecraft.getInstance().renderGlobal;
    ParticleManager particlemanager = Minecraft.getInstance().particles;
    GlStateManager.enableCull();
    GlStateManager.viewport( 0, 0, this.buffer.framebufferWidth, this.buffer.framebufferHeight );
    this.fogRender.updateFogColor( partialTicks );
    GlStateManager.clear( 16640 );
    this.setupCameraTransform( partialTicks );
    ActiveRenderInfo.updateRenderInfo( Minecraft.getInstance().getRenderViewEntity(),
        Minecraft.getInstance().gameSettings.thirdPersonView == 2,
        this.farPlaneDistance );
    ClippingHelperImpl.getInstance();
    ICamera icamera = new Frustum();
    Entity entity = Minecraft.getInstance().getRenderViewEntity();
    double d0 = entity.lastTickPosX + ( entity.posX - entity.lastTickPosX ) * partialTicks;
    double d1 = entity.lastTickPosY + ( entity.posY - entity.lastTickPosY ) * partialTicks;
    double d2 = entity.lastTickPosZ + ( entity.posZ - entity.lastTickPosZ ) * partialTicks;
    icamera.setPosition( d0, d1, d2 );
    if ( Minecraft.getInstance().gameSettings.renderDistanceChunks >= 4 )
    {
      this.fogRender.setupFog( -1, partialTicks );
      GlStateManager.matrixMode( 5889 );
      GlStateManager.loadIdentity();
      GlStateManager.multMatrixf( Matrix4f.perspective( fov,
          (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferHeight, 0.05F,
          this.farPlaneDistance * 2.0F ) );
      GlStateManager.matrixMode( 5888 );
      worldrenderer.renderSky( partialTicks );
      GlStateManager.matrixMode( 5889 );
      GlStateManager.loadIdentity();
      GlStateManager.multMatrixf( Matrix4f.perspective( fov,
          (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferHeight, 0.05F,
          this.farPlaneDistance * MathHelper.SQRT_2 ) );
      GlStateManager.matrixMode( 5888 );
    }

    this.fogRender.setupFog( 0, partialTicks );
    GlStateManager.shadeModel( 7425 );
    if ( entity.posY + entity.getEyeHeight() < 128.0D )
    {
      this.renderCloudsCheck( worldrenderer, partialTicks, d0, d1, d2 );
    }

    this.fogRender.setupFog( 0, partialTicks );
    Minecraft.getInstance().getTextureManager().bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
    RenderHelper.disableStandardItemLighting();
    worldrenderer.setupTerrain( entity, partialTicks, icamera, this.frameCount++ , false );
    Minecraft.getInstance().renderGlobal.updateChunks( finishTimeNano );
    GlStateManager.matrixMode( 5888 );
    GlStateManager.pushMatrix();
    GlStateManager.disableAlphaTest();
    worldrenderer.renderBlockLayer( BlockRenderLayer.SOLID, partialTicks, entity );
    GlStateManager.enableAlphaTest();
    Minecraft.getInstance().getTextureManager().getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE ).setBlurMipmap( false,
        Minecraft.getInstance().gameSettings.mipmapLevels > 0 );
    worldrenderer.renderBlockLayer( BlockRenderLayer.CUTOUT_MIPPED, partialTicks, entity );
    Minecraft.getInstance().getTextureManager().getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE ).restoreLastBlurMipmap();
    Minecraft.getInstance().getTextureManager().getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE ).setBlurMipmap( false, false );
    worldrenderer.renderBlockLayer( BlockRenderLayer.CUTOUT, partialTicks, entity );
    Minecraft.getInstance().getTextureManager().getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE ).restoreLastBlurMipmap();
    GlStateManager.shadeModel( 7424 );
    GlStateManager.alphaFunc( 516, 0.1F );
    GlStateManager.matrixMode( 5888 );
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    RenderHelper.enableStandardItemLighting();
    worldrenderer.renderEntities( entity, icamera, partialTicks );
    RenderHelper.disableStandardItemLighting();
    this.lightMap.disableLightmap();
    GlStateManager.matrixMode( 5888 );
    GlStateManager.popMatrix();
    GlStateManager.enableBlend();
    GlStateManager.blendFuncSeparate( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO );
    Minecraft.getInstance().getTextureManager().getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE ).setBlurMipmap( false, false );
    worldrenderer.drawBlockDamageTexture( Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTicks );
    Minecraft.getInstance().getTextureManager().getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE ).restoreLastBlurMipmap();
    GlStateManager.disableBlend();
    this.lightMap.enableLightmap();
    particlemanager.renderLitParticles( entity, partialTicks );
    RenderHelper.disableStandardItemLighting();
    this.fogRender.setupFog( 0, partialTicks );
    particlemanager.renderParticles( entity, partialTicks );
    this.lightMap.disableLightmap();
    GlStateManager.depthMask( false );
    GlStateManager.enableCull();
    this.renderRainSnow( partialTicks );
    GlStateManager.depthMask( true );
    worldrenderer.renderWorldBorder( entity, partialTicks );
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
    GlStateManager.blendFuncSeparate( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO );
    GlStateManager.alphaFunc( 516, 0.1F );
    this.fogRender.setupFog( 0, partialTicks );
    GlStateManager.enableBlend();
    GlStateManager.depthMask( false );
    Minecraft.getInstance().getTextureManager().bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
    GlStateManager.shadeModel( 7425 );
    worldrenderer.renderBlockLayer( BlockRenderLayer.TRANSLUCENT, partialTicks, entity );
    GlStateManager.shadeModel( 7424 );
    GlStateManager.depthMask( true );
    GlStateManager.enableCull();
    GlStateManager.disableBlend();
    GlStateManager.disableFog();
    if ( entity.posY + entity.getEyeHeight() >= 128.0D )
    {
      this.renderCloudsCheck( worldrenderer, partialTicks, d0, d1, d2 );
    }
    GlStateManager.clear( 256 );
    this.renderHand( partialTicks );
  }

  private void renderHand( float partialTicks )
  {
    GlStateManager.matrixMode( 5889 );
    GlStateManager.loadIdentity();
    GlStateManager.multMatrixf( Matrix4f.perspective( this.fov,
        (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferHeight, 0.05F,
        this.farPlaneDistance * 2.0F ) );
    GlStateManager.matrixMode( 5888 );
    GlStateManager.loadIdentity();
    GlStateManager.pushMatrix();
    this.hurtCameraEffect( partialTicks );
    if ( Minecraft.getInstance().gameSettings.viewBobbing )
    {
      this.applyBobbing( partialTicks );
    }

    boolean flag =
        Minecraft.getInstance().getRenderViewEntity() instanceof EntityLivingBase
            && ( (EntityLivingBase) Minecraft.getInstance().getRenderViewEntity() ).isPlayerSleeping();
    if ( !net.minecraftforge.client.ForgeHooksClient.renderFirstPersonHand( Minecraft.getInstance().renderGlobal, partialTicks ) )
    {
      if ( Minecraft.getInstance().gameSettings.thirdPersonView == 0 && !flag && !Minecraft.getInstance().gameSettings.hideGUI
          && Minecraft.getInstance().playerController.getCurrentGameType() != GameType.SPECTATOR )
      {
        this.lightMap.enableLightmap();
        Minecraft.getInstance().entityRenderer.itemRenderer.renderItemInFirstPerson( partialTicks );
        this.lightMap.disableLightmap();
      }
    }

    GlStateManager.popMatrix();
    if ( Minecraft.getInstance().gameSettings.thirdPersonView == 0 && !flag )
    {
      Minecraft.getInstance().entityRenderer.itemRenderer.renderOverlays( partialTicks );
      this.hurtCameraEffect( partialTicks );
    }

    if ( Minecraft.getInstance().gameSettings.viewBobbing )
    {
      this.applyBobbing( partialTicks );
    }
  }

  /**
   * Render rain and snow
   */
  protected void renderRainSnow( float partialTicks )
  {
    net.minecraftforge.client.IRenderHandler renderer = Minecraft.getInstance().world.getDimension().getWeatherRenderer();
    if ( renderer != null )
    {
      renderer.render( partialTicks, Minecraft.getInstance().world, Minecraft.getInstance() );
      return;
    }
    float f = Minecraft.getInstance().world.getRainStrength( partialTicks );
    if ( !( f <= 0.0F ) )
    {
      this.lightMap.enableLightmap();
      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      World world = Minecraft.getInstance().world;
      int i = MathHelper.floor( entity.posX );
      int j = MathHelper.floor( entity.posY );
      int k = MathHelper.floor( entity.posZ );
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.disableCull();
      GlStateManager.normal3f( 0.0F, 1.0F, 0.0F );
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
          GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO );
      GlStateManager.alphaFunc( 516, 0.1F );
      double d0 = entity.lastTickPosX + ( entity.posX - entity.lastTickPosX ) * partialTicks;
      double d1 = entity.lastTickPosY + ( entity.posY - entity.lastTickPosY ) * partialTicks;
      double d2 = entity.lastTickPosZ + ( entity.posZ - entity.lastTickPosZ ) * partialTicks;
      int l = MathHelper.floor( d1 );
      int i1 = 5;
      if ( Minecraft.getInstance().gameSettings.fancyGraphics )
      {
        i1 = 10;
      }

      int j1 = -1;
      float f1 = partialTicks;
      bufferbuilder.setTranslation( -d0, -d1, -d2 );
      GlStateManager.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for ( int k1 = k - i1; k1 <= k + i1; ++k1 )
      {
        for ( int l1 = i - i1; l1 <= i + i1; ++l1 )
        {
          int i2 = ( k1 - k + 16 ) * 32 + l1 - i + 16;
          double d3 = this.rainXCoords[ i2 ] * 0.5D;
          double d4 = this.rainYCoords[ i2 ] * 0.5D;
          blockpos$mutableblockpos.setPos( l1, 0, k1 );
          Biome biome = world.getBiome( blockpos$mutableblockpos );
          if ( biome.getPrecipitation() != Biome.RainType.NONE )
          {
            int j2 = world.getHeight( Heightmap.Type.MOTION_BLOCKING, blockpos$mutableblockpos ).getY();
            int k2 = j - i1;
            int l2 = j + i1;
            if ( k2 < j2 )
            {
              k2 = j2;
            }

            if ( l2 < j2 )
            {
              l2 = j2;
            }

            int i3 = j2;
            if ( j2 < l )
            {
              i3 = l;
            }

            if ( k2 != l2 )
            {
              this.random.setSeed( l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761 );
              blockpos$mutableblockpos.setPos( l1, k2, k1 );
              float f2 = biome.getTemperature( blockpos$mutableblockpos );
              if ( f2 >= 0.15F )
              {
                if ( j1 != 0 )
                {
                  if ( j1 >= 0 )
                  {
                    tessellator.draw();
                  }

                  j1 = 0;
                  Minecraft.getInstance().getTextureManager().bindTexture( RAIN_TEXTURES );
                  bufferbuilder.begin( 7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP );
                }

                double d5 =
                    -( (double) ( l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31 ) + partialTicks )
                        / 32.0D * ( 3.0D + this.random.nextDouble() );
                double d6 = l1 + 0.5F - entity.posX;
                double d7 = k1 + 0.5F - entity.posZ;
                float f3 = MathHelper.sqrt( d6 * d6 + d7 * d7 ) / i1;
                float f4 = ( ( 1.0F - f3 * f3 ) * 0.5F + 0.5F ) * f;
                blockpos$mutableblockpos.setPos( l1, i3, k1 );
                int j3 = world.getCombinedLight( blockpos$mutableblockpos, 0 );
                int k3 = j3 >> 16 & '\uffff';
                int l3 = j3 & '\uffff';
                bufferbuilder.pos( l1 - d3 + 0.5D, l2, k1 - d4 + 0.5D ).tex( 0.0D, k2 * 0.25D + d5 ).color( 1.0F, 1.0F, 1.0F, f4 ).lightmap( k3, l3 )
                    .endVertex();
                bufferbuilder.pos( l1 + d3 + 0.5D, l2, k1 + d4 + 0.5D ).tex( 1.0D, k2 * 0.25D + d5 ).color( 1.0F, 1.0F, 1.0F, f4 ).lightmap( k3, l3 )
                    .endVertex();
                bufferbuilder.pos( l1 + d3 + 0.5D, k2, k1 + d4 + 0.5D ).tex( 1.0D, l2 * 0.25D + d5 ).color( 1.0F, 1.0F, 1.0F, f4 ).lightmap( k3, l3 )
                    .endVertex();
                bufferbuilder.pos( l1 - d3 + 0.5D, k2, k1 - d4 + 0.5D ).tex( 0.0D, l2 * 0.25D + d5 ).color( 1.0F, 1.0F, 1.0F, f4 ).lightmap( k3, l3 )
                    .endVertex();
              }
              else
              {
                if ( j1 != 1 )
                {
                  if ( j1 >= 0 )
                  {
                    tessellator.draw();
                  }

                  j1 = 1;
                  Minecraft.getInstance().getTextureManager().bindTexture( SNOW_TEXTURES );
                  bufferbuilder.begin( 7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP );
                }

                double d8 = -( ( 0 & 511 ) + partialTicks ) / 512.0F;
                double d9 = this.random.nextDouble() + f1 * 0.01D * ( (float) this.random.nextGaussian() );
                double d10 = this.random.nextDouble() + f1 * (float) this.random.nextGaussian() * 0.001D;
                double d11 = l1 + 0.5F - entity.posX;
                double d12 = k1 + 0.5F - entity.posZ;
                float f6 = MathHelper.sqrt( d11 * d11 + d12 * d12 ) / i1;
                float f5 = ( ( 1.0F - f6 * f6 ) * 0.3F + 0.5F ) * f;
                blockpos$mutableblockpos.setPos( l1, i3, k1 );
                int i4 = ( world.getCombinedLight( blockpos$mutableblockpos, 0 ) * 3 + 15728880 ) / 4;
                int j4 = i4 >> 16 & '\uffff';
                int k4 = i4 & '\uffff';
                bufferbuilder.pos( l1 - d3 + 0.5D, l2, k1 - d4 + 0.5D ).tex( 0.0D + d9, k2 * 0.25D + d8 + d10 ).color( 1.0F, 1.0F, 1.0F, f5 )
                    .lightmap( j4, k4 ).endVertex();
                bufferbuilder.pos( l1 + d3 + 0.5D, l2, k1 + d4 + 0.5D ).tex( 1.0D + d9, k2 * 0.25D + d8 + d10 ).color( 1.0F, 1.0F, 1.0F, f5 )
                    .lightmap( j4, k4 ).endVertex();
                bufferbuilder.pos( l1 + d3 + 0.5D, k2, k1 + d4 + 0.5D ).tex( 1.0D + d9, l2 * 0.25D + d8 + d10 ).color( 1.0F, 1.0F, 1.0F, f5 )
                    .lightmap( j4, k4 ).endVertex();
                bufferbuilder.pos( l1 - d3 + 0.5D, k2, k1 - d4 + 0.5D ).tex( 0.0D + d9, l2 * 0.25D + d8 + d10 ).color( 1.0F, 1.0F, 1.0F, f5 )
                    .lightmap( j4, k4 ).endVertex();
              }
            }
          }
        }
      }

      if ( j1 >= 0 )
      {
        tessellator.draw();
      }

      bufferbuilder.setTranslation( 0.0D, 0.0D, 0.0D );
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc( 516, 0.1F );
      this.lightMap.disableLightmap();
    }
  }

  private void setupCameraTransform( float partialTicks )
  {
    this.farPlaneDistance = Minecraft.getInstance().gameSettings.renderDistanceChunks * 16;
    GlStateManager.matrixMode( 5889 );
    GlStateManager.loadIdentity();

    GlStateManager.multMatrixf( Matrix4f.perspective( fov,
        (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferHeight, 0.05F,
        farPlaneDistance * MathHelper.SQRT_2 ) );
    GlStateManager.matrixMode( 5888 );
    GlStateManager.loadIdentity();
    this.hurtCameraEffect( partialTicks );
    if ( Minecraft.getInstance().gameSettings.viewBobbing )
    {
      this.applyBobbing( partialTicks );
    }

    // float f = this.mc.player.prevTimeInPortal + ( this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal ) *
    // partialTicks;
    // if ( f > 0.0F )
    // {
    // int i = 20;
    // if ( this.mc.player.isPotionActive( MobEffects.NAUSEA ) )
    // {
    // i = 7;
    // }

    // float f1 = 5.0F / ( f * f + 5.0F ) - f * 0.04F;
    // f1 = f1 * f1;
    // GlStateManager.rotatef( ( (float) this.rendererUpdateCount + partialTicks ) * i, 0.0F, 1.0F, 1.0F );
    // GlStateManager.scalef( 1.0F / f1, 1.0F, 1.0F );
    // GlStateManager.rotatef( -( (float) this.rendererUpdateCount + partialTicks ) * i, 0.0F, 1.0F, 1.0F );
    // }

    this.orientCamera( partialTicks );
  }

  /**
   * sets up player's eye (or camera in third person mode)
   */
  private void orientCamera( float partialTicks )
  {
    Entity entity = Minecraft.getInstance().getRenderViewEntity();
    float f = entity.getEyeHeight();
    if ( entity instanceof EntityLivingBase && ( (EntityLivingBase) entity ).isPlayerSleeping() )
    {
      f = (float) ( f + 1.0D );
      GlStateManager.translatef( 0.0F, 0.3F, 0.0F );
    }
    else
    {
      GlStateManager.translatef( 0.0F, 0.0F, 0.05F );
    }

    float yaw = entity.getYaw( partialTicks ) + 180F;
    float pitch = entity.getPitch( partialTicks );
    float roll = 0.0F;
    GlStateManager.rotatef( roll, 0.0F, 0.0F, 1.0F );
    GlStateManager.rotatef( pitch, 1.0F, 0.0F, 0.0F );
    GlStateManager.rotatef( yaw, 0.0F, 1.0F, 0.0F );

    GlStateManager.translatef( 0.0F, -f, 0.0F );
  }

  /**
   * Updates the bobbing render effect of the player.
   */
  private void applyBobbing( float partialTicks )
  {
    if ( Minecraft.getInstance().getRenderViewEntity() instanceof EntityPlayer )
    {
      EntityPlayer entityplayer = (EntityPlayer) Minecraft.getInstance().getRenderViewEntity();
      float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
      float f1 = -( entityplayer.distanceWalkedModified + f * partialTicks );
      float f2 = entityplayer.prevCameraYaw + ( entityplayer.cameraYaw - entityplayer.prevCameraYaw ) * partialTicks;
      float f3 = entityplayer.prevCameraPitch + ( entityplayer.cameraPitch - entityplayer.prevCameraPitch ) * partialTicks;
      GlStateManager.translatef( MathHelper.sin( f1 * (float) Math.PI ) * f2 * 0.5F, -Math.abs( MathHelper.cos( f1 * (float) Math.PI ) * f2 ), 0.0F );
      GlStateManager.rotatef( MathHelper.sin( f1 * (float) Math.PI ) * f2 * 3.0F, 0.0F, 0.0F, 1.0F );
      GlStateManager.rotatef( Math.abs( MathHelper.cos( f1 * (float) Math.PI - 0.2F ) * f2 ) * 5.0F, 1.0F, 0.0F, 0.0F );
      GlStateManager.rotatef( f3, 1.0F, 0.0F, 0.0F );
    }
  }

  private void hurtCameraEffect( float partialTicks )
  {
    if ( Minecraft.getInstance().getRenderViewEntity() instanceof EntityLivingBase )
    {
      EntityLivingBase entitylivingbase = (EntityLivingBase) Minecraft.getInstance().getRenderViewEntity();
      float f = entitylivingbase.hurtTime - partialTicks;
      if ( entitylivingbase.getHealth() <= 0.0F )
      {
        float f1 = entitylivingbase.deathTime + partialTicks;
        GlStateManager.rotatef( 40.0F - 8000.0F / ( f1 + 200.0F ), 0.0F, 0.0F, 1.0F );
      }

      if ( f < 0.0F )
      {
        return;
      }

      f = f / entitylivingbase.maxHurtTime;
      f = MathHelper.sin( f * f * f * f * (float) Math.PI );
      float f2 = entitylivingbase.attackedAtYaw;
      GlStateManager.rotatef( -f2, 0.0F, 1.0F, 0.0F );
      GlStateManager.rotatef( -f * 14.0F, 0.0F, 0.0F, 1.0F );
      GlStateManager.rotatef( f2, 0.0F, 1.0F, 0.0F );
    }

  }

  private void renderCloudsCheck( WorldRenderer renderGlobalIn, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ )
  {
    if ( Minecraft.getInstance().gameSettings.shouldRenderClouds() != 0 )
    {
      GlStateManager.matrixMode( 5889 );
      GlStateManager.loadIdentity();
      GlStateManager.multMatrixf( Matrix4f.perspective( this.fov,
          (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferHeight, 0.05F,
          this.farPlaneDistance * 4.0F ) );
      GlStateManager.matrixMode( 5888 );
      GlStateManager.pushMatrix();
      this.fogRender.setupFog( 0, partialTicks );
      renderGlobalIn.renderClouds( partialTicks, viewEntityX, viewEntityY, viewEntityZ );
      GlStateManager.disableFog();
      GlStateManager.popMatrix();
      GlStateManager.matrixMode( 5889 );
      GlStateManager.loadIdentity();
      GlStateManager.multMatrixf( Matrix4f.perspective( this.fov,
          (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferHeight, 0.05F,
          this.farPlaneDistance * MathHelper.SQRT_2 ) );
      GlStateManager.matrixMode( 5888 );
    }
  }

  public Framebuffer getFrameBuffer()
  {
    return this.buffer;
  }
}
