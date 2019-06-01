package com.evolution.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.evolution.EvolutionLife;

public class SocketManager
{
  private Socket socket;
  private boolean isListening;

  private OutputStream out;
  private InputStream in;

  private int id;

  /**
   * Creates a new socket manager for this client and listens to data being sent
   *
   * @param inSocket
   * @param id - The index of this client in the client array
   * @throws IOException
   */
  public SocketManager( Socket inSocket, int id ) throws IOException
  {
    this.isListening = true;
    this.out = inSocket.getOutputStream();
    this.in = inSocket.getInputStream();
    this.id = id;

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
          }
        }

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

    EvolutionLife.manager.server.handleInPacket( buffer, this.id );
  }

  /**
   * Sends a byte array to this client
   *
   * @param buffer
   */
  public void sendBytes( byte[] buffer )
  {
    try
    {
      this.out.write( buffer );
      this.out.flush();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Closes the socket and cleans up the client
   */
  public void close()
  {
    this.isListening = false;
  }
}
