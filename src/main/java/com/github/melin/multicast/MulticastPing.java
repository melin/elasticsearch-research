/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.melin.common.io.stream.BytesStreamInput;
import com.github.melin.common.io.stream.BytesStreamOutput;
import com.github.melin.common.io.stream.CachedStreamInput;
import com.github.melin.common.io.stream.HandlesStreamOutput;
import com.github.melin.common.io.stream.StreamInput;
import com.github.melin.common.io.stream.StreamOutput;

/**
 * Create on @2013-12-20 @下午4:32:39 
 * @author bsli@ustcinfo.com
 */
public class MulticastPing {
	private static final Logger LOGGER = LoggerFactory.getLogger(MulticastPing.class);
	
	private static final byte[] INTERNAL_HEADER = new byte[]{1, 9, 8, 4};
	
	private final int bufferSize = 2048;
	private final int port = 54328;
	private final String group = "224.2.2.4";
	private final int ttl = 3;
	
	private volatile Receiver receiver;
    private volatile Thread receiverThread;
	private volatile MulticastSocket multicastSocket;
	private DatagramPacket datagramPacketSend;
    private DatagramPacket datagramPacketReceive;
    
    private final Object sendMutex = new Object();
    private final Object receiveMutex = new Object();
    
    public static void main(String[] args) throws Exception {
    	MulticastPing ping = new MulticastPing();
    	ping.start();
    	
    	for(;;) {
    		ping.ping();
    		TimeUnit.SECONDS.sleep(5);
    	}
	}
	
	private void start() throws Exception {
        try {
            this.datagramPacketReceive = new DatagramPacket(new byte[bufferSize], bufferSize);
            this.datagramPacketSend = new DatagramPacket(new byte[bufferSize], bufferSize, InetAddress.getByName(group), port);
        } catch (Exception e) {
        	LOGGER.warn("disabled, failed to setup multicast (datagram) discovery : {}", e.getMessage());
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug("disabled, failed to setup multicast (datagram) discovery", e);
            }
            return;
        }

        InetAddress multicastInterface = null;
        try {
         	MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.setTimeToLive(ttl);

            /*multicastInterface = networkService.resolvePublishHostAddress(address);*/
            multicastSocket.setInterface(InetAddress.getByName("127.0.0.1"));
            multicastSocket.joinGroup(InetAddress.getByName(group));

            multicastSocket.setReceiveBufferSize(bufferSize);
            multicastSocket.setSendBufferSize(bufferSize);
            multicastSocket.setSoTimeout(60000);

            this.multicastSocket = multicastSocket;

            this.receiver = new Receiver();
            receiverThread = new Thread(receiver, "discovery#multicast#receiver");
            receiverThread.setDaemon(true);
            this.receiverThread.start();
        } catch (Exception e) {
            datagramPacketReceive = null;
            datagramPacketSend = null;
            if (multicastSocket != null) {
                multicastSocket.close();
                multicastSocket = null;
            }
            LOGGER.warn("disabled, failed to setup multicast discovery on port [{}], [{}]: {}", port, multicastInterface, e.getMessage());
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug("disabled, failed to setup multicast discovery on {}", e, multicastInterface);
            }
        }
    }
	
	private void ping() {
        if (multicastSocket == null) {
            return;
        }
        synchronized (sendMutex) {
            try {
                BytesStreamOutput bStream = new BytesStreamOutput();
                StreamOutput out = new HandlesStreamOutput(bStream);
                out.writeBytes(INTERNAL_HEADER);
                
                PingRequest pr = new PingRequest();
                pr.setMsg("ping");
                pr.writeTo(out);
                
                out.close();
                datagramPacketSend.setData(bStream.bytes().toBytes());
                multicastSocket.send(datagramPacketSend);
                if (LOGGER.isTraceEnabled()) {
                	LOGGER.trace("sending ping request");
                }
            } catch (Exception e) {
                LOGGER.warn("failed to send multicast ping request", e);
            }
        }
    }
	
	private void stop() throws Exception {
        if (receiver != null) {
            receiver.stop();
        }
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
        if (multicastSocket != null) {
            multicastSocket.close();
            multicastSocket = null;
        }
    }
	
	private class Receiver implements Runnable {

        private volatile boolean running = true;

        public void stop() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    synchronized (receiveMutex) {
                        try {
                            multicastSocket.receive(datagramPacketReceive);
                        } catch (SocketTimeoutException ignore) {
                            continue;
                        } catch (Exception e) {
                        	running = false;
                        	LOGGER.warn("failed to receive packet, throttling...", e);
                        }
                        try {
                            boolean internal = false;
                            if (datagramPacketReceive.getLength() > 4) {
                                int counter = 0;
                                for (; counter < INTERNAL_HEADER.length; counter++) {
                                    if (datagramPacketReceive.getData()[datagramPacketReceive.getOffset() + counter] != INTERNAL_HEADER[counter]) {
                                        break;
                                    }
                                }
                                if (counter == INTERNAL_HEADER.length) {
                                    internal = true;
                                }
                            }
                            if (internal) {
                                StreamInput input = CachedStreamInput.cachedHandles(new BytesStreamInput(datagramPacketReceive.getData(), datagramPacketReceive.getOffset() + INTERNAL_HEADER.length, datagramPacketReceive.getLength(), true));
                                PingRequest pr = new PingRequest();
                                pr.readFrom(input);
                                System.out.println(pr);
                            } else {
                               throw new IllegalStateException("failed multicast message, probably message from previous version");
                            }
                        } catch (Exception e) {
                        	LOGGER.warn("failed to read requesting data from {}", e, datagramPacketReceive.getSocketAddress());
                            continue;
                        }
                    }
                } catch (Exception e) {
                    if (running) {
                    	LOGGER.warn("unexpected exception in multicast receiver", e);
                    }
                }
            }
        }
    }
}
