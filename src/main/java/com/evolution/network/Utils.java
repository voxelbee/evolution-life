package com.evolution.network;

public class Utils
{
  /**
   * Reads in an int from the specified buffer
   * 
   * @param b - Buffer
   * @param readerIndex - Current index that reading from
   * @return
   */
  public static int byteArrayToInt( byte[] b, int readerIndex )
  {
    return b[ readerIndex + 3 ] & 0xFF |
        ( b[ readerIndex + 2 ] & 0xFF ) << 8 |
        ( b[ readerIndex + 1 ] & 0xFF ) << 16 |
        ( b[ readerIndex ] & 0xFF ) << 24;
  }

  public static byte[] intToByteArray( int a )
  {
    return new byte[] {
        (byte) ( ( a >> 24 ) & 0xFF ),
        (byte) ( ( a >> 16 ) & 0xFF ),
        (byte) ( ( a >> 8 ) & 0xFF ),
        (byte) ( a & 0xFF )
    };
  }
}
