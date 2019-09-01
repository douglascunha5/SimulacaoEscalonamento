package politicas;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import objetos.Arquivo;
import objetos.Processo;

public abstract class Politica {
	private int multiprogramacao, cpr, tcp, tpr, tempo, indiceProcessos;
	private ArrayList<Processo> processos;
	private ArrayList<Processo> es, prontos, memoria;
	private Processo cpu;

	public Politica(Arquivo arquivo) {
		this.multiprogramacao = arquivo.getMultiprogramacao();
		this.cpr = arquivo.getCpr();
		this.tcp = arquivo.getTcp();
		this.tpr = arquivo.getTpr();
		this.processos = arquivo.getProcessos();
		this.prontos = new ArrayList<Processo>();
		this.es = new ArrayList<Processo>();
		this.memoria = new ArrayList<Processo>();
		this.indiceProcessos = 0;
	}

	public void calcular() {

		for (Processo processo : getProcessos()) {
			processo.setTempoEspera(0);
			processo.setCpuUsada(0);
			processo.setFezES(false);
			processo.setQuandoEntrou(0);
		}

		while (getMemoria().size() < getMultiprogramacao() && getIndiceProcessos() < getProcessos().size()) {
			criarProcesso(getProcessos().get(getIndiceProcessos()));
		}

		// Enquanto houverem processos na fila de prontos, ser� feita uma troca de
		// contexto
		while (prontos.size() != 0) {
			trocaContexto(prontos.get(0));
		}

		// If adicionado para quando s� resta o �ltimo processo na mem�ria.
		if ((prontos.size() == 0 && es.size() > 0)) {
			saiES();
			trocaContexto(prontos.get(0));
		}

		calculaTempoEspera();
		escreveSaida();
	}

	// Fun��o para criar processos e coloc�-los na fila de prontos.
	public void criarProcesso(Processo processo) {
		if (memoria.size() < multiprogramacao) {
			memoria.add(processo);

			// If criado para verificar se algum processo da ES deve ir para a fila de
			// prontos antes do processo que est� sendo criado.
			if (es.size() != 0) {
				int menorEs = menorES();
				if (es.get(menorEs).getTempoEspera() + es.get(menorEs).getEs() < tempo + cpr) {
					saiES();
				}
			}

			prontos.add(processo);
			this.tempo += cpr;
			processo.setTempoEspera(tempo);
			indiceProcessos++;
		}
	}

	// M�todo que simula a utiliza��o de cpu

	public void usaCpu(Processo processo) {
		// Se o processo ainda n�o tiver feito ES ele executa a sua CPU1 e faz ES.
		if (!processo.isFezES()) {
			tempo += processo.getCpu1();
			processo.setTempoEspera(tempo);

			// Verifica se algum processo sai da ES antes de um processo que est� na CPU
			// terminar.
			if (es.size() != 0) {
				int saemEs = saemES(); // Vari�vel que ir� receber o quantos processos devem sair da ES.

				// Se algum processo sair da ES antes do processo que est� na cpu terminar
				// executa a fun��o saiES().
				if (saemEs > 0) {
					for (int i = 0; i < saemEs; i++) {
						saiES();
					}
				} else { // Sen�o o processo que est� na cpu vai para a es.
					fazES(processo);
				}
			} else {
				fazES(processo);
			}

		}
		// Sen�o ele executa sua CPU2 e encerra.
		else {
			tempo += processo.getCpu2();
			// Verifica se algum processo sai da ES antes de um processo que est� na CPU
			// terminar.
			if (es.size() != 0) {
				int saemEs = saemES(); // Vari�vel que ir� receber o quantos processos devem sair da ES.

				// Se algum processo sair da ES antes do processo que est� na cpu terminar
				// executa a fun��o saiES().
				if (saemEs > 0) {
					for (int i = 0; i < saemEs; i++) {
						saiES();
					}
				} else { // Sen�o o processo que est� na cpu ser� encerrado.
					encerraProcesso(processo);
				}
			} else {
				cpu = null;
				encerraProcesso(processo);
			}
		}
	}

	// M�todo que simula uma troca de contexto do processo.
	public void trocaContexto(Processo processo) {
		// Se a cpu n�o estiver ocupada o processo assume a cpu.
		if (cpu == null) {
			tempo += tcp;
			prontos.remove(0);
			cpu = processo;
			usaCpu(processo);
		}
		// Sen�o verifica-se se o processo que est� na cpu j� fez ES.
		else {
			// Se j� fez ele encerra.
			if (cpu.isFezES() == true) {
				encerraProcesso(cpu);
			}
			// Sen�o ele faz ES.
			else {
				fazES(cpu);
			}
		}
	}

	// M�todo para encerrar um processo.
	public void encerraProcesso(Processo processo) {
		cpu = null;
		tempo += tpr;
		processo.setTempoEspera(tempo);

		// For que verifica qual processo deve sair da mem�ria.
		for (int i = 0; i < memoria.size(); i++) {
			if (memoria.get(i).getNome().compareTo(processo.getNome()) == 0)
				memoria.remove(i);
		}
		// Se ainda houverem processos no escalonador de longo prazo a fun��o
		// criarProcesso � chamada.
		if (indiceProcessos < processos.size())
			criarProcesso(processos.get(indiceProcessos));
	}

	// M�todo que simula a entrada/saida de um processo.
	public void fazES(Processo processo) {
		es.add(processo);
		cpu = null;

		// Verifica se algum processo da fila de prontos ir� usar cpu por completo antes
		// de algum sair da ES.
		if (prontos.size() != 0) {
			trocaContexto(prontos.get(0));
		}
		// Sen�o a fun��o saiEs() � chamada.
		else {
			saiES();
		}
	}

	// M�todo que calcula qual processo sair� primeiro da ES.
	public int menorES() {
		int menor = es.get(0).getTempoEspera() + es.get(0).getEs();
		int indice = 0;
		for (int i = 0; i < es.size(); i++) {
			if ((es.get(i).getTempoEspera() + es.get(i).getEs()) < menor) {
				menor = (es.get(i).getTempoEspera() + es.get(i).getEs());
				indice = i;
			}
		}
		return indice; // Retornar� o �ndice que cont�m o processo que sair� primeiro.
	}

	// Fun��o que verifica quais processos devem sair da ES antes do t�rmino do uso
	// da CPU
	public int saemES() {
		int quantosSaem = 0;
		for (int i = 0; i < es.size(); i++) {
			if (es.get(i).getTempoEspera() + es.get(i).getEs() <= tempo) {
				quantosSaem++;
			}
		}
		return quantosSaem; // Retornar� quantos processos devem sair.
	}

	// M�todo que ir� tirar o pr�ximo processo da ES.
	public void saiES() {
		int quemSai = menorES();
		Processo processo = es.get(quemSai);
		es.remove(quemSai);
		processo.setFezES(true);
		if (cpu == null) {
			tempo = processo.getEs() + processo.getTempoEspera();
		}
		prontos.add(processo);

	}

	// Fun��o que calcula o tempo de espera de cada processo.
	public void calculaTempoEspera() {
		for (Processo p : processos) {
			p.setTempoEspera(p.getTempoEspera() - (p.getCpu1() + p.getCpu2() + p.getEs()));
		}
	}

	// Fun��o que calcula o tempo m�dio de espera.
	public double calculaTME() {
		double tme = 0;
		for (Processo p : processos) {
			tme += p.getTempoEspera();
		}
		tme = tme / processos.size();

		return tme;
	}

	// Faz um bubble sort para reordenar os processos por ordem de nome.
	public void reordena(ArrayList<Processo> processos) {
		Processo aux;
		for (int i = 0; i < processos.size(); i++) {
			for (int j = 0; j < processos.size() - 1; j++) {
				if (processos.get(j).getNome().compareTo(processos.get(j + 1).getNome()) > 0) {
					aux = processos.get(j);
					processos.set(j, processos.get(j + 1));
					processos.set(j + 1, aux);
				}
			}
		}
	}

	// Fun��o para escrever o arquivo de sa�da.
	public void escreveSaida() {
		// Chama m�todo para reordenar os processos de acordo com nome.
		reordena(processos);
		try {
			FileWriter arquivo = new FileWriter("out.txt");
			PrintWriter escritor = new PrintWriter(arquivo);

			escritor.println("FCFS");
			for (int i = 0; i < processos.size(); i++) {
				escritor.println("-TE-" + processos.get(i).getNome() + "=" + processos.get(i).getTempoEspera());
			}
			escritor.println("-TME=" + calculaTME());

			escritor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMultiprogramacao() {
		return multiprogramacao;
	}

	public void setMultiprogramacao(int multiprogramacao) {
		this.multiprogramacao = multiprogramacao;
	}

	public int getCpr() {
		return cpr;
	}

	public void setCpr(int cpr) {
		this.cpr = cpr;
	}

	public int getTcp() {
		return tcp;
	}

	public void setTcp(int tcp) {
		this.tcp = tcp;
	}

	public int getTpr() {
		return tpr;
	}

	public void setTpr(int tpr) {
		this.tpr = tpr;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public int getIndiceProcessos() {
		return indiceProcessos;
	}

	public void setIndiceProcessos(int indiceProcessos) {
		this.indiceProcessos = indiceProcessos;
	}

	public ArrayList<Processo> getProcessos() {
		return processos;
	}

	public void setProcessos(ArrayList<Processo> processos) {
		this.processos = processos;
	}

	public ArrayList<Processo> getEs() {
		return es;
	}

	public void setEs(ArrayList<Processo> es) {
		this.es = es;
	}

	public ArrayList<Processo> getProntos() {
		return prontos;
	}

	public void setProntos(ArrayList<Processo> prontos) {
		this.prontos = prontos;
	}

	public ArrayList<Processo> getMemoria() {
		return memoria;
	}

	public void setMemoria(ArrayList<Processo> memoria) {
		this.memoria = memoria;
	}

	public Processo getCpu() {
		return cpu;
	}

	public void setCpu(Processo cpu) {
		this.cpu = cpu;
	}

}
