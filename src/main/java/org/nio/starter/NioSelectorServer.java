package org.nio.starter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioSelectorServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.bind(new InetSocketAddress("localhost", 8889));

    // selector to query events from OS then update to selected keys
    Selector selector = Selector.open();

    // 0. register to event1 "someone want to connect"
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    System.out.println("server started on port 8889");

    while (true) {
      // 1. query events from OS then update selected keys
      System.out.println("query event from OS");
      selector.select();
      System.out.println("found at least one event");
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = selectionKeys.iterator();

      // 2. process all selected keys
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        // on event1 fired
        if (key.isAcceptable()) {
          System.out.println("someone going to connect");
          ServerSocketChannel server = (ServerSocketChannel) key.channel();
          SocketChannel socketChannel = server.accept();
          socketChannel.configureBlocking(false);
          // register event2 "someone have data to read"
          socketChannel.register(selector, SelectionKey.OP_READ);
          System.out.println("client connected, registered event when data available to read");

        } else if (key.isReadable()) { // on event2 fired
          System.out.println("something readable");
          SocketChannel socket = (SocketChannel) key.channel();
          ByteBuffer byteBuffer = ByteBuffer.allocate(128);
          int len = socket.read(byteBuffer);
          if (len > 0) {
            System.out.println("got data from socket " + new String(byteBuffer.array(), StandardCharsets.UTF_8));
          } else if (len == -1) {
            System.out.println("somehow socket disconnected");
          }
        }
        // remove key, mean event processed
        System.out.println("processed event, remove it");
        iterator.remove();
      }
    }
  }
}
