package com.evolution.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import com.evolution.EvolutionLife;
import com.evolution.network.packet.DNAPacket;

public class DNAGenerator implements Runnable
{
  private static final int DNA_LENGTH = 1024;

  private BlockingQueue< DNATasks > tasks = new LinkedBlockingQueue< DNATasks >();

  private Map< UUID, byte[] > DNABank = new HashMap< UUID, byte[] >();
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
          DNABank.put( task.entityID, dna );
          this.sendDNAPacket( task.entityID, dna );
        }
      }
      catch ( InterruptedException e )
      {

      }
    }
  }

  /**
   * Sends the dna packet to the client that owns this entity
   */
  private void sendDNAPacket( UUID entityID, byte[] dna )
  {
    UUID clientID = ( (EntityAI) EvolutionLife.mcServer.getPlayerList().getPlayerByUUID( entityID ) ).owner;
    EvolutionLife.manager.dispatchServer.sendToClient( new DNAPacket( dna, entityID ), clientID );
  }

  public byte[] generateNewRandom()
  {
    byte[] dna = new byte[ DNA_LENGTH / 4 ];
    ThreadLocalRandom.current().nextBytes( dna );
    return dna;
  }

  /**
   * Creates new random DNA and then sends it to the client controller
   *
   * @param entityId
   */
  public void addNewRandom( UUID entityId )
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
