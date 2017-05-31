import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EcoAssincrono {
	public static void main(String[] args) {
		try {
			final AsynchronousServerSocketChannel listener;
			listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));
			listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
				public void completed(AsynchronousSocketChannel ch, Void att) {
					listener.accept(null, this);
					ch.write(ByteBuffer.wrap("Envie uma frase: ".getBytes()));
					ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
					try {
						int bytesRead = ch.read(byteBuffer).get(20, TimeUnit.SECONDS);
						boolean running = true;
						while (bytesRead != -1 && running) {
							System.out.println("bytes read: " + bytesRead);
							if (byteBuffer.position() > 2) {
								byteBuffer.flip();
								byte[] lineBytes = new byte[bytesRead];
								byteBuffer.get(lineBytes, 0, bytesRead);
								String line = new String(lineBytes);
								System.out.println("Message: " + line);
								ch.write(ByteBuffer.wrap(line.getBytes()));
								byteBuffer.clear();
								bytesRead = ch.read(byteBuffer).get(20, TimeUnit.SECONDS);
							} else {
								running = false;
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						ch.write(ByteBuffer.wrap("\nAté logo!\n".getBytes()));
					}
					System.out.println("Fim da conversa!");
					try {
						if (ch.isOpen()) {
							ch.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				public void failed(Throwable exc, Void att) {
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}