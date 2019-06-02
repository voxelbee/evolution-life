package com.evolution.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import com.evolution.network.packet.AIPacket;
import com.evolution.network.packet.EnumPacketTypes;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SocketManager
{
  private Socket socket;
  private boolean isListening;

  private OutputStream out;
  private InputStream in;

  private UUID clientId;

  /**
   * Creates a new socket manager for this client and listens to data being sent
   *
   * @param inSocket
   * @param id - The index of this client in the client array
   * @throws IOException
   */
  public SocketManager( Socket inSocket, UUID id ) throws IOException
  {
    this.isListening = true;
    this.out = inSocket.getOutputStream();
    this.in = inSocket.getInputStream();
    this.clientId = id;

    ( new Thread( new Runnable()
    {

      @Override
      public void run()
      {
        while ( isListening )
        {
          try
          {
            listen();
          }
          catch ( IOException e )
          {
            e.printStackTrace();
            isListening = false;
          }
        }
      }
    } ) ).start();
  }

  /**
   * Listens to incoming buffers from the client
   *
   * @throws IOException
   */
  private void listen() throws IOException
  {
    byte[] number = new byte[ 4 ];
    this.in.read( number );
    int dataSize = Utils.byteArrayToInt( number, 0 );

    byte[] buffer = new byte[ dataSize ];
    this.in.read( buffer );

    ByteBuf buf = Unpooled.wrappedBuffer( buffer );

    AIPacket packet = EnumPacketTypes.PacketTypes.getPacketFromID( buf.readInt() );
    packet.readPacket( buf );
    MainThreadPacketHandler.handlePacket( packet, this.clientId );
    buf.release();
  }

  /**
   * Sends a byte array to this client
   *
   * @param buffer
   */
  private void sendBytes( ByteBuf buffer )
  {
    try
    {
      this.out.write( buffer.readableBytes() );
      buffer.getBytes( 0, this.out, buffer.readableBytes() );
      this.out.flush();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Sends the packet to the server
   *
   * @param packet
   */
  public void sendPacket( AIPacket packet )
  {
    ByteBuf buf = Unpooled.directBuffer( 8 );
    buf.writeInt( EnumPacketTypes.PacketTypes.getIdFromPacket( packet ) ); // Writes the id to the buffer
    packet.writePacket( buf );
    this.sendBytes( buf );
    buf.release();
  }

  /**
   * Closes the socket and cleans up the client
   */
  public void close()
  {
    this.isListening = false;

    // Close the socket
    try
    {
      socket.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
  }
}
