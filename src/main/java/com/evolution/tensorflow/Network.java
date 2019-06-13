package com.evolution.tensorflow;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.Session;
import org.tensorflow.Shape;
import org.tensorflow.Tensor;

import com.evolution.network.EvolutionLifePacketHandler;
import com.evolution.network.FinishedProcessPacket;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.renderer.texture.NativeImage;

public class Network implements Runnable
{
  private BlockingQueue< NativeImage > tasks = new LinkedBlockingQueue< NativeImage >();

  private List< Tensor< Float > > weights = new ArrayList< Tensor< Float > >();
  private List< Tensor< Float > > biases = new ArrayList< Tensor< Float > >();

  private FloatBuffer imageBuf;
  private FloatBuffer outputs;

  private UUID organismId;

  private float forwards;
  private float strafe;
  private float yaw;
  private float pitch;
  private boolean jump;

  private boolean isRunning;

  private int inputSize;
  private int outputSize;

  private byte[] dna;

  public Network( UUID inorganismId, int inInputSize, int inOutputSize, byte[] inDna )
  {
    this.inputSize = inInputSize * inInputSize * 3;
    this.outputSize = inOutputSize;
    this.dna = inDna;

    this.imageBuf = FloatBuffer.allocate( this.inputSize );
    this.outputs = FloatBuffer.allocate( this.outputSize );

    this.organismId = inorganismId;

    Thread thread = new Thread( this );
    thread.setName( "Network Calculation" );
    thread.setDaemon( true );
    thread.start();
  }

  @Override
  public void run()
  {
    Session sess = new Session( this.generateNetworkGraph() );
    this.isRunning = true;
    while ( this.isRunning )
    {
      try
      {
        NativeImage task = this.tasks.poll( 500, TimeUnit.MILLISECONDS );
        if ( task != null )
        {
          this.calculate( task, sess );
        }
      }
      catch ( InterruptedException e )
      {
        e.printStackTrace();
      }
    }

    for ( int i = 0; i < this.weights.size(); i++ )
    {
      this.weights.get( i ).close();
      this.biases.get( i ).close();
    }
    sess.close();
  }

  public Graph generateNetworkGraph()
  {
    Graph graph = new Graph();
    int[] networkDims = new int[] { 1000, 100, 10 }; // TODO: Controlled by genes

    Operation image = graph.opBuilder( "Placeholder", "input" )
        .setAttr( "dtype", DataType.FLOAT )
        .setAttr( "shape", Shape.make( -1, this.inputSize ) )
        .build();

    Operation layer = buildLayer( graph, image, this.inputSize, networkDims[ 0 ], "layerFirst" );
    for ( int i = 0; i < networkDims.length - 1; i++ )
    {
      layer = buildLayer( graph, layer, networkDims[ i ], networkDims[ i + 1 ], "layer_" + i );
    }
    buildLayer( graph, layer, networkDims[ networkDims.length - 1 ], this.outputSize, "output" );
    return graph;
  }

  private Operation buildLayer( Graph graph,
      Operation previousLayer,
      int inLayerDimensions,
      int outLayerDimensions,
      String layerName )
  {
    FloatBuffer weightBuffer = FloatBuffer.allocate( inLayerDimensions * outLayerDimensions );
    for ( int i = 0; i < inLayerDimensions * outLayerDimensions; i++ )
    {
      weightBuffer.put( (float) ThreadLocalRandom.current().nextDouble( -1.0, 1.0 ) ); // TODO: Controlled by genes
    }
    weightBuffer.rewind();
    this.weights.add( Tensor.create( new long[] { inLayerDimensions, outLayerDimensions }, weightBuffer ) );

    FloatBuffer biasBuffer = FloatBuffer.allocate( outLayerDimensions );
    for ( int i = 0; i < outLayerDimensions; i++ )
    {
      biasBuffer.put( (float) ThreadLocalRandom.current().nextDouble( -1.0, 1.0 ) ); // TODO: Controlled by genes
    }
    biasBuffer.rewind();
    this.biases.add( Tensor.create( new long[] { outLayerDimensions }, biasBuffer ) );

    Operation weightConst = graph.opBuilder( "Const", layerName + "_weightConst" )
        .setAttr( "dtype", DataType.FLOAT )
        .setAttr( "value", this.weights.get( this.weights.size() - 1 ) )
        .build();

    Operation biasConst = graph.opBuilder( "Const", layerName + "_biasConst" )
        .setAttr( "dtype", DataType.FLOAT )
        .setAttr( "value", this.biases.get( this.biases.size() - 1 ) )
        .build();

    Operation weightMult = graph.opBuilder( "MatMul", layerName + "_weightMul" )
        .addInput( previousLayer.output( 0 ) )
        .addInput( weightConst.output( 0 ) )
        .build();

    Operation biasAdd = graph.opBuilder( "Add", layerName + "_biasAdd" )
        .addInput( weightMult.output( 0 ) )
        .addInput( biasConst.output( 0 ) )
        .build();

    Operation layer = graph.opBuilder( "Sigmoid", layerName )
        .addInput( biasAdd.output( 0 ) )
        .build();

    return layer;
  }

  private void calculate( NativeImage task, Session sess )
  {
    this.imageBuf.rewind();
    this.outputs.rewind();
    for ( int i = 0; i < task.getWidth(); i++ )
    {
      for ( int j = 0; j < task.getHeight(); j++ )
      {
        int pixel = task.getPixelRGBA( i, j );
        int alpha = (byte) ( pixel >>> 24 );
        int blue = ( (byte) ( pixel >>> 16 ) ) & 0xFF;
        int green = ( (byte) ( pixel >>> 8 ) ) & 0xFF;
        int red = ( (byte) pixel ) & 0xFF;
        this.imageBuf.put( blue / 255.0f );
        this.imageBuf.put( green / 255.0f );
        this.imageBuf.put( red / 255.0f );
      }
    }
    this.imageBuf.rewind();

    Tensor< Float > image = Tensor.create( new long[] { 1, this.inputSize }, imageBuf );
    sess.runner().feed( "input", image ).fetch( "output" ).run().get( 0 ).expect( Float.class ).writeTo( this.outputs );
    this.outputs.rewind();
    image.close();

    this.forwards = this.outputs.get() - 0.5f;
    this.strafe = this.outputs.get() - 0.5f;
    this.yaw = this.outputs.get() * 360.0f;
    this.pitch = ( this.outputs.get() - 0.5f ) * 180.0f;
    if ( this.outputs.get() > 0.5 )
    {
      this.jump = true;
    }
    else
    {
      this.jump = false;
    }
    this.sendInputs();
  }

  private void sendInputs()
  {
    EvolutionLifePacketHandler.INSTANCE.sendToServer( new FinishedProcessPacket( this.organismId,
        this.forwards,
        this.strafe,
        this.yaw,
        this.pitch,
        this.jump ) );
  }

  public void stop()
  {
    this.isRunning = false;
  }

  public void addTask( NativeImage image )
  {
    if ( this.isRunning )
    {
      this.tasks.add( image );
    }
  }
}
