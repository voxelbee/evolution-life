package com.evolution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AIServer implements Runnable
{
  private ServerSocket server;
  private boolean runServer;

  public AIServer( String ipAddress, int port ) throws Exception
  {
    if ( ipAddress != null && !ipAddress.isEmpty() )
    {
      this.server = new ServerSocket( port, 1, InetAddress.getByName( ipAddress ) );
    }
    else
    {
      this.server = new ServerSocket( port, 1, InetAddress.getLocalHost() );
    }
    this.runServer = true;
  }

  private void listen() throws Exception
  {
    String data = null;
    Socket client = this.server.accept();
    String clientAddress = client.getInetAddress().getHostAddress();
    System.out.println( "\r\nNew connection from " + clientAddress );

    BufferedReader in = new BufferedReader(
        new InputStreamReader( client.getInputStream() ) );
    while ( ( data = in.readLine() ) != null )
    {
      System.out.println( "\r\nMessage from " + clientAddress + ": " + data );
      EvolutionLife.manager.spawnCount += Integer.valueOf( data );
    }
  }

  public InetAddress getSocketAddress()
  {
    return this.server.getInetAddress();
  }

  public int getPort()
  {
    return this.server.getLocalPort();
  }

  @Override
  public void run()
  {
    while ( runServer )
    {
      try
      {
        this.listen();
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }

  public void close()
  {
    this.runServer = false;
  }
}
