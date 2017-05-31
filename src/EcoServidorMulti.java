import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import static java.lang.System.out;

public class EcoServidorMulti {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket;
		serverSocket = new ServerSocket(4444);
		while (true) {
			out.println("Esperando conexão...");
			Socket conexao = serverSocket.accept();
			Thread t = new Thread(new EcoTask(conexao));
			t.start();
		}
	}
}

class EcoTask implements Runnable {
	private Socket conexao;

	public EcoTask(Socket c) {
		conexao = c;
	}

	public void run() {
		try {
			InputStream is = conexao.getInputStream();
			OutputStream os = conexao.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			PrintWriter pw = new PrintWriter(os);
			pw.print("Digite o seu nome: ");
			pw.flush();
			String nome = br.readLine();
			String linha;
			while (true) {
				out.println("Esperando usuário digitar...");
				pw.print("Digite sua mensagem: ");
				pw.flush();
				linha = br.readLine();
				out.println("Mensagem recebida!");
				pw.println("\n" + nome + " diz: " + linha);
				pw.flush();
			}
		} catch (Exception e) {
			if (conexao != null) {
				try {
					conexao.close();
				} catch (Exception ex) {
				}
			}
		}
	}
}
