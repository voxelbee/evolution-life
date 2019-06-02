package com.evolution.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.evolution.network.packet.AIPacket;

public class ServerManager
{
  private boolean isRunning;
  private Map< UUID, Client > clients = new HashMap< UUID, Client >();
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
    Socket socket = server.accept();
    UUID id = UUID.randomUUID();
    this.clients.put( id, new Client( new SocketManager( socket, id ), id ) );
  }

  /**
   * Sends a packet to a client
   *
   * @param buf - The data to send
   * @param id - The id of the client to send the data to
   */
  public void sendToClient( AIPacket packet, UUID id )
  {
    this.clients.get( id ).getSocket().sendPacket( packet );
  }

  /**
   * Closes all of the sockets and the server.
   */
  public void close()
  {
    this.isRunning = false;

    // Closes all of the client sockets
    for ( UUID key : clients.keySet() )
    {
      clients.remove( key ).getSocket().close();
    }

    try
    {
      this.server.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Updates all of the clients and there owned entities.
   */
  public void tickClients()
  {
    for ( UUID key : clients.keySet() )
    {
      clients.get( key ).tick();
    }
  }

  /**
   * Returns the client with the specified UUID.
   *
   * @param id
   * @return
   */
  public Client getClient( UUID id )
  {
    return this.clients.get( id );
  }
}
