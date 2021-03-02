package de.ancash.ilibrary.sockets;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ancash.ilibrary.misc.SerializationUtils;

import static java.nio.channels.SelectionKey.*;

public class Test {

	public static void main(String[] args) throws Exception {
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
	    Selector selector = Selector.open();
	    
	    new Thread(() ->{
	    	
	    	int clients = 1;
	    	List<ChatClient> ccs = new ArrayList<ChatClient>();
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    for(int i = 0; i<clients; i++) {
		    	ChatClient cc = new ChatClient("localhost", 25700, "lol") {
					
					@Override
					public void onPacket(Packet req) {
						System.out.println("paacekt");
					}
				};
				ccs.add(cc);
		    }
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    for(int i = 0; i<10; i++) {
		    	ccs.forEach(c -> c.send(new Packet("d", "hello")));
		    	System.out.println(i);
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
	    	
	    }).start();
	    
	    ServerSocketChannel server1 = ServerSocketChannel.open();
	    server1.configureBlocking(false);
	    server1.socket().bind(new InetSocketAddress("localhost", 25700));
	    server1.register(selector, OP_ACCEPT);

	    while (true) {
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
	                    break;
	                case OP_READ:
	                    client = (SocketChannel) key.channel();
	                    buffer.clear();
	                    if (client.read(buffer) != -1) {
	                        buffer.flip();
	                        //sString line = new String(buffer.array(), buffer.position(), buffer.remaining());
	                        Packet p = (Packet) SerializationUtils.deserializeFromBytes(buffer.array());
	                        client.write(ByteBuffer.wrap(SerializationUtils.serialize(p)));
	                        /*if (line.startsWith("CLOSE")) {
	                            client.close();
	                        } else if (line.startsWith("QUIT")) {
	                            for (SelectionKey k : selector.keys()) {
	                                k.cancel();
	                                k.channel().close();
	                            }
	                            selector.close();
	                            return;
	                        }*/
	                    } else {
	                        key.cancel();
	                    }
	                    break;
	                default:
	                    System.out.println("unhandled " + key.readyOps());
	                    break;
	            }
	        }
	    }
	}
	
}
