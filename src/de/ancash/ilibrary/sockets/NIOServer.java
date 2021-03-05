package de.ancash.ilibrary.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static java.nio.channels.SelectionKey.*;

public class NIOServer {

	private final ServerSocketChannel server;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private Selector selector = Selector.open();
	private Thread thread;
    
	public NIOServer(String host, int port) throws IOException {
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.socket().bind(new InetSocketAddress(host, port));
		server.register(selector, OP_ACCEPT);
	}
	
	@SuppressWarnings("deprecation")
	public void stop() throws IOException {
		server.socket().close();
		server.close();
		selector.close();
		thread.stop();
		thread = null;
	}
	
	public void start() {
		System.out.println("Startin Server Socket Channel...");
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (server.isOpen() && thread != null && selector.isOpen()) {
				        selector.select();
				        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
				        while (iter.hasNext()) {
				            SocketChannel client;
				            SelectionKey key = iter.next();
				            iter.remove();

				            switch (key.readyOps()) {
				                case OP_ACCEPT:
				                    client = ((ServerSocketChannel) key.channel()).accept();
				                    client.configureBlocking(false);
				                    client.register(selector, OP_READ);
				                    System.out.println("Accepted " + client.socket().getRemoteSocketAddress());
				                    break;
				                case OP_READ:
				                    client = (SocketChannel) key.channel();
				                    buffer.clear();
				                    if (client.read(buffer) != -1) {
				                        buffer.flip();
				                        
				                        for (SelectionKey k : selector.keys()) {
				                        	if(k.channel() instanceof SocketChannel && !k.equals(key)) ((SocketChannel) k.channel()).write(ByteBuffer.wrap(buffer.array()));
			                            }
				                    } else {
				                        key.cancel();
				                    }
				                    break;
				                default:
				                    System.out.println("unhandled " + key.readyOps());
				                    break;
				            }
				        }
				        Thread.sleep(1);
				    }
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}, "ServerSocketChannel");
		thread.start();
	}
}
