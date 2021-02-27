package de.ancash.ilibrary.datastructures.buffers;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CircularBuffer{
   private char[] _buffer;
   public final int _buffer_size;
   private int _write_index = 0;
   private int _read_index = 0;
   private AtomicInteger _readable_data = new AtomicInteger(0);
   
   public CircularBuffer(int buffer_size) {
      if (!IsPowerOfTwo(buffer_size)) {
         throw new IllegalArgumentException();
      }
      this._buffer_size = buffer_size;
      _buffer = new char[buffer_size];
   }

   private boolean IsPowerOfTwo(int i) {
      return (i & (i - 1)) == 0;
   }

   private int getTrueIndex(int i) {
      return i % _buffer_size;
   }

   public Character readOutChar() {
      Character result = null;

      // if we have data to read
      if (_readable_data.get() > 0) {

         result = Character.valueOf(_buffer[getTrueIndex(_read_index)]);
         _readable_data.decrementAndGet();
         _read_index++;
      }

      return result;
   }

   public boolean writeToCharBuffer(char c) {
      boolean result = false;

      // if we can write to the buffer
      if (_readable_data.get() < _buffer_size) {
         // write to buffer
         _buffer[getTrueIndex(_write_index)] = c;
         _readable_data.incrementAndGet();
         _write_index++;
         result = true;
      }

      return result;
   }

   public static class BufferWriteWorker extends BukkitRunnable{
      String _alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
      Random _random = new Random();
      CircularBuffer _buffer;
      @SuppressWarnings("unused")
      private final Plugin plugin;
      
      public BufferWriteWorker(CircularBuffer cb, Plugin plugin) {
         this._buffer = cb;
         this.plugin = plugin;
         this.runTaskTimerAsynchronously(plugin, 1, 2);
      }

      public void stop() {
    	  this.cancel();
      }
      
      private char getRandomChar() {
         return _alphabet.charAt(_random.nextInt(_alphabet.length()));
      }
      
      @Override
      public void run() {
    	  if(Thread.interrupted()) this.cancel();
    	  	if (!_buffer.writeToCharBuffer(getRandomChar())) {
    	  		Thread.yield();
    	  		try {
    	  			Thread.sleep(10);
    	  		} catch (InterruptedException e) {
    	  			return;
				}
			}
         /*while (!Thread.interrupted()) {
            if (!_buffer.writeToCharBuffer(getRandomChar())) {
               Thread.yield();
               try {
                  Thread.sleep(10);
               } catch (InterruptedException e) {
                  return;
               }
            }
         }*/
      }
   }

   public static class BufferReadWorker extends BukkitRunnable {
      CircularBuffer _buffer;
      @SuppressWarnings("unused")
      private final Plugin plugin;
      
      public BufferReadWorker(CircularBuffer cb, Plugin plugin) {
    	 this.plugin = plugin;
         this._buffer = cb;
         this.runTaskTimerAsynchronously(plugin, 1, 2);
      }

      public void stop() {
    	  this.cancel();
      }
      
      @Override
      public void run() {
         System.out.println("Printing Buffer:");
         if(Thread.interrupted()) this.cancel();
			
			Character c = _buffer.readOutChar();
         if (c != null) {
            System.out.print(c.charValue());
         } else {
            Thread.yield();
            try {
               this.wait(10);
            } catch (InterruptedException e) {
               System.out.println();
               return;
            }
         }
         /*while (!Thread.interrupted()) {
            Character c = _buffer.readOutChar();
            if (c != null) {
               System.out.print(c.charValue());
            } else {
               Thread.yield();
               try {
                  Thread.sleep(10);
               } catch (InterruptedException e) {
                  System.out.println();
                  return;
               }
            }
         }*/
      }
   }

   /*public static void main(String[] args) throws InterruptedException {
      int buffer_size = 1024;
      // create circular buffer
      CircularBuffer cb = new CircularBuffer(buffer_size);

      // create threads that read and write the buffer.
      Thread write_thread = new Thread(new TestWriteWorker(cb));
      Thread read_thread = new Thread(new TestReadWorker(cb));
      read_thread.start();
      write_thread.start();

      // wait some amount of time
      Thread.sleep(10000);

      // interrupt threads and exit
      write_thread.interrupt();
      read_thread.interrupt();
   }*/
}
