package com.evolution.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

public class BufferUtils
{
  /**
   * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant bit
   * dictates whether another byte should be read.
   */
  public static int readVarInt( ByteBuf buf )
  {
    int i = 0;
    int j = 0;

    while ( true )
    {
      byte b0 = buf.readByte();
      i |= ( b0 & 127 ) << j++ * 7;
      if ( j > 5 )
      {
        throw new RuntimeException( "VarInt too big" );
      }

      if ( ( b0 & 128 ) != 128 )
      {
        break;
      }
    }

    return i;
  }

  public static long readVarLong( ByteBuf buf )
  {
    long i = 0L;
    int j = 0;

    while ( true )
    {
      byte b0 = buf.readByte();
      i |= (long) ( b0 & 127 ) << j++ * 7;
      if ( j > 10 )
      {
        throw new RuntimeException( "VarLong too big" );
      }

      if ( ( b0 & 128 ) != 128 )
      {
        break;
      }
    }

    return i;
  }

  public static void writeUniqueId( ByteBuf buf, UUID uuid )
  {
    buf.writeLong( uuid.getMostSignificantBits() );
    buf.writeLong( uuid.getLeastSignificantBits() );
  }

  public static UUID readUniqueId( ByteBuf buf )
  {
    return new UUID( buf.readLong(), buf.readLong() );
  }

  /**
   * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of each
   * such byte only 7 bits will be used to describe the actual value since its most significant bit dictates whether
   * the next byte is part of that same int. Micro-optimization for int values that are expected to have values below
   * 128.
   */
  public static void writeVarInt( ByteBuf buf, int input )
  {
    while ( ( input & -128 ) != 0 )
    {
      buf.writeByte( input & 127 | 128 );
      input >>>= 7;
    }

    buf.writeByte( input );
  }

  public static void writeVarLong( ByteBuf buf, long value )
  {
    while ( ( value & -128L ) != 0L )
    {
      buf.writeByte( (int) ( value & 127L ) | 128 );
      value >>>= 7;
    }

    buf.writeByte( (int) value );
  }

  /**
   * Calculates the number of bytes required to fit the supplied int (0-5) if it were to be read/written using
   * readVarIntFromBuffer or writeVarIntToBuffer
   */
  public static int getVarIntSize( int input )
  {
    for ( int i = 1; i < 5; ++i )
    {
      if ( ( input & -1 << i * 7 ) == 0 )
      {
        return i;
      }
    }

    return 5;
  }
}
