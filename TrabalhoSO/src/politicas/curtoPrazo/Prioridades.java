package politicas.curtoPrazo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import objetos.Arquivo;
import objetos.Processo;
import politicas.Politica;

/**
 * @author Douglas Soares
 *
 */

public class Prioridades extends Politica {

	public Prioridades(Arquivo arquivo) {
		super(arquivo);
	}

	@Override
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

		// Enquanto houverem processos na fila de prontos, será feita uma troca de
		// contexto
		while (getProntos().size() != 0) {
			trocaContexto(escolheProcesso());
		}

		// If adicionado para quando só resta o último processo na memória.
		if ((getProntos().size() == 0 && getEs().size() > 0)) {
			saiES();
		}

		calculaTempoEspera();
		escreveSaida();
	}

	// Método que simula a utilização de cpu
	@Override
	public void usaCpu(Processo processo) {
		processo.setQuandoEntrou(getTempo());
		// Se o processo ainda não tiver feito ES ele executa a sua CPU1 e faz ES.
		if (!processo.isFezES()) {
			setTempo(getTempo() + (processo.getCpu1() - processo.getCpuUsada()));
			processo.setTempoEspera(getTempo());

			// Verifica se algum processo sai da ES antes de um processo que está na CPU
			// terminar.
			if (getEs().size() != 0) {
				int saemEs = saemES(); // Variável que irá receber quantos processos devem sair da ES.

				// Se algum processo sai da ES antes do processo que está na cpu terminar
				// executa a função saiES().
				if (saemEs > 0) {
					saiES();
				} else { // Senão o processo que está na cpu vai para a es.
					fazES(processo);
				}
			} else {
				fazES(processo);
			}

		}
		// Senão ele executa sua CPU2.
		else {
			setTempo(getTempo() + (processo.getCpu2() - processo.getCpuUsada()));
			// Verifica se algum processo sai da ES antes de um processo que está na CPU
			// terminar.
			if (getEs().size() != 0) {
				int saemEs = saemES(); // Variável que irá receber o quantos processos devem sair da ES.

				// Se algum processo sair da ES antes do processo que está na cpu terminar
				// executa a função saiES().
				if (saemEs > 0) {
					for (int i = 0; i < saemEs; i++) {
						saiES();
					}
				} else { // Senão o processo que está na cpu será encerrado.
					encerraProcesso(processo);
				}
			} else {
				setCpu(null);
				encerraProcesso(processo);
			}
		}
	}

	// Método para decidir qual processo deve ir para a troca de contexto
	private int escolheProcesso() {
		int processoEscolhido = 0;
		for (int i = 0; i < getProntos().size(); i++) {
			if (getProntos().get(i).getPrioridade() < getProntos().get(processoEscolhido).getPrioridade())
				processoEscolhido = i;
		}

		return processoEscolhido;
	}

	// Método que simula uma troca de contexto do processo.
	public void trocaContexto(int i) {
		Processo processo = getProntos().get(i);
		// Se a cpu não estiver ocupada, o processo assume a cpu.
		if (getCpu() == null) {
			setTempo(getTempo() + getTcp());
			getProntos().remove(i);
			setCpu(processo);
			usaCpu(processo);
		}
		// Senão verifica-se se o processo que está na cpu já fez ES.
		else {
			// Se já fez ele encerra.
			if (getCpu().isFezES() == true) {
				encerraProcesso(getCpu());
			}
			// Senão ele faz ES.
			else {
				fazES(getCpu());
			}
		}
	}

	// Método que simula uma troca de contexto por preempção.
	private void trocaContextoPreemptada(Processo processo) {
		getProntos().remove(escolheProcesso());

		// Salva o tempo que o processo preemptado já usou de cpu.
		if (getCpu().isFezES() == false) {
			getCpu().setCpuUsada(getCpu().getCpuUsada() + (getTempo() - getCpu().getQuandoEntrou()));
		} else {
			getCpu().setCpuUsada(
					getCpu().getCpuUsada() + (getCpu().getCpu2() - (getTempo() - getCpu().getQuandoEntrou())));
		}

		setTempo(getTempo() + getTcp());
		getCpu().setTempoEspera(getTempo());
		getProntos().add(getCpu());
		setCpu(processo);
		usaCpu(processo);
	}

	// Método que simula a entrada/saida de um processo.
	@Override
	public void fazES(Processo processo) {
		getEs().add(processo);

		processo.setCpuUsada(0); // Zera a cpu já usada do processo para que ela começe a valer para a cpu2.
		setCpu(null);

		// Verifica se existem processos que desejam usar a cpu.
		if (getProntos().size() != 0) {

			trocaContexto(escolheProcesso());
		}
		// Senão a função saiEs() é chamada.
		else {
			saiES();
		}
	}

	// Método que irá tirar o próximo processo da ES.
	@Override
	public void saiES() {
		int quemSai = menorES();
		Processo processo = getEs().get(quemSai);
		getEs().remove(quemSai);
		processo.setFezES(true);
		if (getCpu() == null) {
			setTempo(processo.getEs() + processo.getTempoEspera());
			getProntos().add(processo);
		} else {
			getProntos().add(processo);
			decidePreempcao();
		}

	}

	// Função que decide se deverá haver uma preempção.
	private void decidePreempcao() {
		Processo processo = getProntos().get(escolheProcesso());
		if (processo.getPrioridade() < getCpu().getPrioridade()) {
			setTempo(processo.getEs() + processo.getTempoEspera());
			trocaContextoPreemptada(processo);
		}
	}

	//Função que escreve no arquivo de saída
	@Override
	public void escreveSaida() {
		// Chama método para reordenar os processos de acordo com nome.
		reordena(getProcessos());
		
			try {
				BufferedWriter arquivo = new BufferedWriter(new FileWriter("out.txt", true));
				
				arquivo.write("Prioridades-CP");
				arquivo.newLine();
				for (int i = 0; i < getProcessos().size(); i++) {
					arquivo.write("-TE-" + getProcessos().get(i).getNome() + "=" + getProcessos().get(i).getTempoEspera());
					arquivo.newLine();
				}
				arquivo.write("-TME=" + calculaTME());
				arquivo.newLine();
				
				arquivo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
	}
}
