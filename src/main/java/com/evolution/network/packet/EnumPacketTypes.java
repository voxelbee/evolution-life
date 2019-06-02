package com.evolution.network.packet;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EnumPacketTypes
{
  CLIENTCOUNT( ClientCountPacket.class ),
  DNA( DNAPacket.class );

  private Class< ? > packetClass;

  EnumPacketTypes( Class< ? > intPacketClass )
  {
    this.packetClass = intPacketClass;
  }

  public static class PacketTypes
  {
    // Register all the packets into an id map
    private static final Map< Class< ? >, Integer > IDMAP;
    static
    {
      IDMAP = new HashMap< Class< ? >, Integer >();
      int index = 0;
      for ( EnumPacketTypes type : EnumSet.allOf( EnumPacketTypes.class ) )
      {
        IDMAP.put( type.packetClass, index );
        index++ ;
      }
    }

    /**
     * Returns you a new instance of a packet from its id.
     *
     * @param id - The id of the packet to get
     * @return
     */
    public static AIPacket getPacketFromID( int id )
    {
      try
      {
        return (AIPacket) EnumPacketTypes.values()[ id ].packetClass.getConstructor().newInstance();
      }
      catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
          | SecurityException e )
      {
        e.printStackTrace();
        throw new RuntimeException( "Couldn't find packet with id: " + id );
      }
    }

    /**
     * Returns you the id of a packet from the packet supplied
     *
     * @param packet - The packet to get id from
     * @return
     */
    public static int getIdFromPacket( AIPacket packet )
    {
      return PacketTypes.IDMAP.get( packet.getClass() );
    }
  }
}
