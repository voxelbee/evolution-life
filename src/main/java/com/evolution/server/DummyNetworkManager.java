package com.evolution.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;

public class DummyNetworkManager extends NetworkManager
{

  public DummyNetworkManager()
  {
    super( null );
  }

  @Override
  /**
   * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
   * connection state (protocol)
   */
  public void setNetHandler( INetHandler handler )
  {

  }
}
