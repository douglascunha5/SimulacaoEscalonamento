package objetos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Douglas Soares
 *
 */
public class Arquivo {
	private static int multiprogramacao, cpr, tcp, tpr;
	private static ArrayList<Processo> processos = new ArrayList<>();

	// Construtor da classe.
	public Arquivo() {
		leArquivo();
	}

	public static void leArquivo() {

		// Variável que receberá a próxima linha a ser processada.
		String linhaLida = "";

		try {
			BufferedReader leitor = new BufferedReader(new FileReader("in.txt"));

			try {
				setMultiprogramacao(Integer.parseInt(leitor.readLine().substring(17)));
				leitor.readLine();

				// Comando para ler todos os processos e colocá-los em um ArrayList
				linhaLida = leitor.readLine();
				while (linhaLida.compareTo(";") != 0) {
					int cpu1, es, cpu2, prioridade;
					String nome;

					nome = linhaLida.substring(0, linhaLida.indexOf('='));
					cpu1 = Integer
							.parseInt(linhaLida.substring(linhaLida.lastIndexOf('=') + 1, linhaLida.indexOf('-')));
					es = Integer.parseInt(linhaLida.substring(linhaLida.indexOf(',') + 1,
							linhaLida.indexOf('-', linhaLida.indexOf(','))));
					cpu2 = Integer
							.parseInt(linhaLida.substring(linhaLida.lastIndexOf(',') + 1, linhaLida.lastIndexOf('-')));
					prioridade = Integer.parseInt(linhaLida.substring(linhaLida.indexOf(';') + 1, linhaLida.length()));

					processos.add(new Processo(nome, cpu1, es, cpu2, prioridade));
					linhaLida = leitor.readLine();
				}

				// Comando feito apenas para mudar a linha
				leitor.readLine();

				// Os próximos comandos irão pegar o tamanho do CPR, TCP e TPR
				linhaLida = leitor.readLine();
				setCpr(Integer.parseInt(linhaLida.substring(4, linhaLida.length())));

				linhaLida = leitor.readLine();
				setTcp(Integer.parseInt(linhaLida.substring(4, linhaLida.length())));

				linhaLida = leitor.readLine();
				setTpr(Integer.parseInt(linhaLida.substring(4, linhaLida.length())));

				leitor.close();

			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public int getMultiprogramacao() {
		return multiprogramacao;
	}

	public int getCpr() {
		return cpr;
	}

	public int getTcp() {
		return tcp;
	}

	public int getTpr() {
		return tpr;
	}

	public ArrayList<Processo> getProcessos() {
		return processos;
	}

	public static void setMultiprogramacao(int multiprogramacao) {
		Arquivo.multiprogramacao = multiprogramacao;
	}

	public static void setCpr(int cpr) {
		Arquivo.cpr = cpr;
	}

	public static void setTcp(int tcp) {
		Arquivo.tcp = tcp;
	}

	public static void setTpr(int tpr) {
		Arquivo.tpr = tpr;
	}

}
