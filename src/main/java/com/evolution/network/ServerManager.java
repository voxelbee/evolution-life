package com.evolution.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.evolution.EvolutionLife;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

        // Closes all of the client sockets
        for ( SocketManager socketManager : clients )
        {
          socketManager.close();
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
  public void sendToClient( byte[] buf, int id )
  {
    this.clients.get( id ).sendBytes( buf );
  }

  /**
   * Handles the receiving of a packet from the inputed client
   *
   * @param buf - In buffer to handle
   * @param client - Client id that the data is from
   */
  public void handleInPacket( byte[] buf, int client )
  {
    ByteBuf buffer = Unpooled.wrappedBuffer( buf );
    int packetType = buffer.readInt();
    if ( packetType == 0 )
    {
      int numberOfClients = buffer.readInt();
      EvolutionLife.manager.spawnCount += numberOfClients;
    }
  }
}
