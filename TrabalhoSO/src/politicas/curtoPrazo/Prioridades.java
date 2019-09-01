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

		// Enquanto houverem processos na fila de prontos, ser� feita uma troca de
		// contexto
		while (getProntos().size() != 0) {
			trocaContexto(escolheProcesso());
		}

		// If adicionado para quando s� resta o �ltimo processo na mem�ria.
		if ((getProntos().size() == 0 && getEs().size() > 0)) {
			saiES();
		}

		calculaTempoEspera();
		escreveSaida();
	}

	// M�todo que simula a utiliza��o de cpu
	@Override
	public void usaCpu(Processo processo) {
		processo.setQuandoEntrou(getTempo());
		// Se o processo ainda n�o tiver feito ES ele executa a sua CPU1 e faz ES.
		if (!processo.isFezES()) {
			setTempo(getTempo() + (processo.getCpu1() - processo.getCpuUsada()));
			processo.setTempoEspera(getTempo());

			// Verifica se algum processo sai da ES antes de um processo que est� na CPU
			// terminar.
			if (getEs().size() != 0) {
				int saemEs = saemES(); // Vari�vel que ir� receber quantos processos devem sair da ES.

				// Se algum processo sai da ES antes do processo que est� na cpu terminar
				// executa a fun��o saiES().
				if (saemEs > 0) {
					saiES();
				} else { // Sen�o o processo que est� na cpu vai para a es.
					fazES(processo);
				}
			} else {
				fazES(processo);
			}

		}
		// Sen�o ele executa sua CPU2.
		else {
			setTempo(getTempo() + (processo.getCpu2() - processo.getCpuUsada()));
			// Verifica se algum processo sai da ES antes de um processo que est� na CPU
			// terminar.
			if (getEs().size() != 0) {
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
				setCpu(null);
				encerraProcesso(processo);
			}
		}
	}

	// M�todo para decidir qual processo deve ir para a troca de contexto
	private int escolheProcesso() {
		int processoEscolhido = 0;
		for (int i = 0; i < getProntos().size(); i++) {
			if (getProntos().get(i).getPrioridade() < getProntos().get(processoEscolhido).getPrioridade())
				processoEscolhido = i;
		}

		return processoEscolhido;
	}

	// M�todo que simula uma troca de contexto do processo.
	public void trocaContexto(int i) {
		Processo processo = getProntos().get(i);
		// Se a cpu n�o estiver ocupada, o processo assume a cpu.
		if (getCpu() == null) {
			setTempo(getTempo() + getTcp());
			getProntos().remove(i);
			setCpu(processo);
			usaCpu(processo);
		}
		// Sen�o verifica-se se o processo que est� na cpu j� fez ES.
		else {
			// Se j� fez ele encerra.
			if (getCpu().isFezES() == true) {
				encerraProcesso(getCpu());
			}
			// Sen�o ele faz ES.
			else {
				fazES(getCpu());
			}
		}
	}

	// M�todo que simula uma troca de contexto por preemp��o.
	private void trocaContextoPreemptada(Processo processo) {
		getProntos().remove(escolheProcesso());

		// Salva o tempo que o processo preemptado j� usou de cpu.
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

	// M�todo que simula a entrada/saida de um processo.
	@Override
	public void fazES(Processo processo) {
		getEs().add(processo);

		processo.setCpuUsada(0); // Zera a cpu j� usada do processo para que ela come�e a valer para a cpu2.
		setCpu(null);

		// Verifica se existem processos que desejam usar a cpu.
		if (getProntos().size() != 0) {

			trocaContexto(escolheProcesso());
		}
		// Sen�o a fun��o saiEs() � chamada.
		else {
			saiES();
		}
	}

	// M�todo que ir� tirar o pr�ximo processo da ES.
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

	// Fun��o que decide se dever� haver uma preemp��o.
	private void decidePreempcao() {
		Processo processo = getProntos().get(escolheProcesso());
		if (processo.getPrioridade() < getCpu().getPrioridade()) {
			setTempo(processo.getEs() + processo.getTempoEspera());
			trocaContextoPreemptada(processo);
		}
	}

	//Fun��o que escreve no arquivo de sa�da
	@Override
	public void escreveSaida() {
		// Chama m�todo para reordenar os processos de acordo com nome.
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
