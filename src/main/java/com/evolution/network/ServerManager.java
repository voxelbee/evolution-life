package com.evolution.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.evolution.network.packet.AIPacket;

public class ServerManager
{
  private boolean isRunning;
  private List< SocketManager > clients = new ArrayList< SocketManager >();
  private ServerSocket server;

  /**
   * Creates a new server and starts the server listening for clients.
   *
   * @param adress - Address to bind to
   * @param port - Port to bind to
   */
  public ServerManager( String adress, int port ) throws UnknownHostException, IOException
  {
    this.server = new ServerSocket( port, 1, InetAddress.getByName( adress ) );

    this.isRunning = true;
    ( new Thread( new Runnable()
    {

      @Override
      public void run()
      {
        while ( isRunning )
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
      }
    } ) ).start();
  }

  /**
   * Accepts new clients and creates the new socket manager for them.
   *
   * @throws IOException
   */
  private void listen() throws IOException
  {
    this.clients.add( new SocketManager( server.accept(), this.clients.size() ) );
  }

  /**
   * Sends a packet to a client
   *
   * @param buf - The data to send
   * @param id - The id of the client to send the data to
   */
  public void sendToClient( AIPacket packet, int id )
  {
    this.clients.get( id ).sendPacket( packet );
  }

  public void close()
  {
    this.isRunning = false;
    // Closes all of the client sockets
    for ( SocketManager socketManager : clients )
    {
      socketManager.close();
    }
  }
}
