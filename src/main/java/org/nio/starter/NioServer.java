package org.nio.starter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioServer {
  static List<SocketChannel> socketChannelList = new ArrayList<>();

  public static void main(String[] args) throws IOException, InterruptedException {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress("localhost", 8888));

    // config non-blocking mode
    serverSocketChannel.configureBlocking(false);
    System.out.println("Start server socket successful on port: 8888");

    while (true) {
      // wait for new connection, non-blocking
      SocketChannel socketChannel = serverSocketChannel.accept();
      if (socketChannel != null) {
        System.out.println("new client socket connected");
        // setting fetch message of current socket to non-blocking too
        socketChannel.configureBlocking(false);
        socketChannelList.add(socketChannel);
      }
//      System.out.println("still looking for new connection");

      Iterator<SocketChannel> iterator = socketChannelList.iterator();

      // run from begin to end of list
      // fixme: not good: if a socket return len=0, still loop over it, waste resource
      while (iterator.hasNext()) {
        SocketChannel sock = iterator.next();
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        // read from sock
        int len = sock.read(byteBuffer);
        if (len > 0) {
          System.out.println("got bytes from sock" + new String(byteBuffer.array()));
        } else if (len == -1) { // somehow, disconnected
          iterator.remove(); // remove socket, need to close it?!
          System.out.println("socket" + sock.getRemoteAddress() + "disconnected");
        }
      }
    }
  }
}
