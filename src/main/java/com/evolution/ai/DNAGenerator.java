package com.evolution.ai;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import com.evolution.EvolutionLife;
import com.evolution.network.packet.PacketDispatchEntity;

public class DNAGenerator implements Runnable
{
  // Length in bits
  private static final int DNA_LENGTH = 1024;

  private BlockingQueue< DNATasks > tasks = new LinkedBlockingQueue< DNATasks >();

  private boolean run;

  @Override
  public void run()
  {
    this.run = true;
    while ( run )
    {
      try
      {
        DNATasks task = tasks.take();
        if ( task.taskType == com.evolution.ai.DNAGenerator.DNATasks.EnumTypes.NEW )
        {
          byte[] dna = this.generateNewRandom();
          ( (EntityAI) EvolutionLife.mcServer.getPlayerList().getPlayerByUUID( task.entityID ) ).setDNA( dna );
          this.sendDNAPacket( task.entityID, dna );
        }
      }
      catch ( InterruptedException e )
      {
        return;
      }
    }
  }

  private void sendDNAPacket( UUID entityID, byte[] dna )
  {
    UUID clientID = ( (EntityAI) EvolutionLife.mcServer.getPlayerList().getPlayerByUUID( entityID ) ).getOwner();
    EvolutionLife.manager.getClientHandler( clientID ).sendPacket( new PacketDispatchEntity( entityID, dna ) );
  }

  private byte[] generateNewRandom()
  {
    byte[] dna = new byte[ DNA_LENGTH / 4 ];
    ThreadLocalRandom.current().nextBytes( dna );
    return dna;
  }

  public void createNewStrand( UUID entityId )
  {
    tasks.add( new DNATasks( com.evolution.ai.DNAGenerator.DNATasks.EnumTypes.NEW, entityId ) );
  }

  public void stop()
  {
    this.run = false;
    this.tasks = null;
  }

  private static class DNATasks
  {
    public enum EnumTypes
    {
      NEW,
      BREED;
    }

    public EnumTypes taskType;
    public UUID entityID;

    public DNATasks( EnumTypes type, UUID inId )
    {
      this.taskType = type;
      this.entityID = inId;
    }
  }
}
