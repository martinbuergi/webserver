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
import java.util.concurrent.TimeUnit;

public class HttpServer {
	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		String root = "/Users/martinbuergi/Documents/dev/ws/html";
		int port = 9000;
		int bufferSize = 1024;

		if (args.length > 0)
			root = args[0];
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
			AsynchronousChannelGroup group = AsynchronousChannelGroup
					.withThreadPool(Executors.newFixedThreadPool(50));

			final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel
					.open().bind(new InetSocketAddress(port));

			System.out.println("Server listening on " + port);

			server.accept("Client connection",
					new CompletionHandler<AsynchronousSocketChannel, Object>() {
						public void completed(AsynchronousSocketChannel ch,
								Object att) {
							try {
								System.out
										.println("Accepted a connection from "
												+ ch.getRemoteAddress()
														.toString());
								server.accept("Client connection", this);
								new Worker(ch).handle();
							} catch (IOException e) {
								System.err.println("I/O error: "
										+ e.getMessage());
							} catch (InterruptedException e) {
								System.err.println("Interrupted: "
										+ e.getMessage());
							} catch (ExecutionException e) {
								System.err.println("Excecution error: "
										+ e.getMessage());
							}
						}

						public void failed(Throwable exc, Object att) {
							System.out.println("Failed to accept connection");
						}
					});

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

		public void handle() throws InterruptedException, ExecutionException,
				IOException {
			HttpRequest request = null;
			HttpResponse response = null;
			request = HttpRequest.Builder.build(ch, root);
			response = HttpResponse.Builder.create(request).build();
			// Write header
			ch.write(ByteBuffer.wrap(response.getHeader().getBytes())).get();

			// Method HEAD does not require message
			if (request.getHTTPMethod().equals(HttpMethod.HEAD)) {
				ch.close();
				return;
			}

			writeMessage(response);

			ch.close();
		}

		private void writeMessage(HttpResponse response)
				throws InterruptedException, ExecutionException {
			AsynchronousFileChannel fileChannel = response.getFileChannel();
			if (fileChannel == null)
				return;

			ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
			int pos = 0;

			while (fileChannel.read(readBuffer, pos).get() >= 0) {
				readBuffer.rewind();
				ch.write(readBuffer).get();
				pos = pos + bufferSize;
				readBuffer.rewind();
			}
		}
	}
}
