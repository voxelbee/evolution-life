package com.evolution.server;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.RequestProcessPacket;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.network.PacketDistributor;

public class DNAGenerator implements Runnable
{
  private BlockingQueue< Task > tasks = new LinkedBlockingQueue< Task >();

  private boolean isRunning;

  private int startSize;

  public enum EnumTaskType
  {
    RANDOM,
    BREED
  }

  public DNAGenerator( int inStartSize )
  {
    this.startSize = inStartSize;

    Thread self = new Thread( this );
    self.setDaemon( true );
    self.setName( "DNAGenerator" );
    self.start();
  }

  @Override
  public void run()
  {
    this.isRunning = true;
    while ( this.isRunning )
    {
      try
      {
        Task task = this.tasks.poll( 500, TimeUnit.MILLISECONDS );
        if ( task != null )
        {
          switch ( task.type )
          {
            case RANDOM:
              this.runRandomTask( task );
              break;
            case BREED:
              this.runBreedTask( task );
              break;
          }
        }
      }
      catch ( InterruptedException e )
      {
        e.printStackTrace();
      }
    }
  }

  private void runRandomTask( Task task )
  {
    byte[] dna = new byte[ this.startSize ];
    ThreadLocalRandom.current().nextBytes( dna );

    EvolutionLifePacketHandler.INSTANCE.send( PacketDistributor.PLAYER.with( () -> task.client ),
        new RequestProcessPacket( task.organism, dna ) );
  }

  private void runBreedTask( Task task )
  {

  }

  public void addTask( EnumTaskType type, EntityPlayerMP client, UUID organism )
  {
    this.tasks.add( new Task( type, client, organism ) );
  }

  public void addTask( EnumTaskType type, EntityPlayerMP client, UUID organism, List< UUID > parents )
  {
    this.tasks.add( new Task( type, client, organism, parents ) );
  }

  public void stop()
  {
    this.isRunning = false;
  }

  private class Task
  {
    public EnumTaskType type;
    public List< UUID > parents;
    public EntityPlayerMP client;
    public UUID organism;

    public Task( EnumTaskType inType, EntityPlayerMP inClient, UUID inOrganism )
    {
      this.type = inType;
      this.client = inClient;
      this.organism = inOrganism;
    }

    public Task( EnumTaskType inType, EntityPlayerMP inClient, UUID inOrganism, List< UUID > inParents )
    {
      this.type = inType;
      this.client = inClient;
      this.parents = inParents;
      this.organism = inOrganism;
    }
  }
}
