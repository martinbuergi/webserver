package com.buergi.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HttpServer {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		int port = 8080;
		int bufferSize = 2*1024;

		if (args.length == 0){
            System.err.println("Please specify document root directory");
            System.exit(1);
          }
			
		String root = args[0];
		
		if (args.length >= 2)
			port = Integer.valueOf(args[1]);
		if (args.length >= 3)
			bufferSize = Integer.valueOf(args[2]);

		HttpServer server = new HttpServer(port, root, bufferSize);
		server.start();
	}

	private String root;
	private int port;
	private int bufferSize;

	public HttpServer(int port, String root, int bufferSize) {
		this.port = port;
		this.root = root;
		this.bufferSize = bufferSize;
	};

	public void start() {
		try {
			AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(50));
			final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(port));

			System.out.println("Server listening on port " + port);
			System.out.println("document root directory is " + root);
			System.out.println("buffersize is " + bufferSize);
			
			server.accept("Client connection",
				new CompletionHandler<AsynchronousSocketChannel, Object>() {
					public void completed(AsynchronousSocketChannel ch, Object att) {
							System.out.println("Accepted a connection");
							server.accept(null, this);
							new Worker(ch).handle();							
					}

					public void failed(Throwable exc, Object att) {
						System.err.println("Failed to accept connection");
					}
				}
			);

			group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.err.println("Interrupted: " + e.getMessage());
		}
	}

	private class Worker {
		private AsynchronousSocketChannel ch;

		public Worker(AsynchronousSocketChannel ch) {
			this.ch = ch;
		};

		public void handle()  {
			try{
				String requestHeader = readChannel();
				if (requestHeader.isEmpty()){
					ch.close();
					return;
				}

				HttpRequest request = new HttpRequest(requestHeader);
				HttpResponse response = new HttpResponse(request, root);

				// Write header
				ch.write(ByteBuffer.wrap(response.getHeader().getBytes())).get();
				
				// Write message
				writeMessage(response);
	
				ch.close();
			} catch (IOException e) {
				System.err.println("I/O error: " + e.getMessage());
			} catch (InterruptedException e) {
				System.err.println("Interrupted: " + e.getMessage());
			} catch (ExecutionException e) {
				System.err.println("Excecution error: " + e.getMessage());
			}
		}

		private String readChannel() throws InterruptedException, ExecutionException{
			ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
			
			StringBuffer sb = new StringBuffer();
			
			int x;
			while ((x = ch.read(readBuffer).get()) != -1) {
				readBuffer.flip();
				sb.append(new String(readBuffer.array()));

				if (x < bufferSize)
					break;
			}
			
			return sb.toString();
		}

		
		private void writeMessage(HttpResponse httpResponse) throws InterruptedException, ExecutionException, IOException {
			// error message?
			if (httpResponse.getErrorMessage() != null) {
				ch.write(ByteBuffer.wrap(httpResponse.getErrorMessage().getBytes()));
				return;
			}

			AsynchronousFileChannel fCh = httpResponse.getFileChannel();
			
			if (fCh == null)
				return;

			
			ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
			int pos = 0;

			while (fCh.read(readBuffer, pos).get() >= 0) {
				readBuffer.flip();
				Future<Integer> future = ch.write(readBuffer);
				while (!future.isDone()){};
				readBuffer.clear();

				pos = pos + bufferSize;
			}
			
			fCh.close();
		}		
	}
}
