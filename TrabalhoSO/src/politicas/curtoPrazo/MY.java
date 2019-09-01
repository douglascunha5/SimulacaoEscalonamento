package politicas.curtoPrazo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import objetos.Arquivo;
import objetos.Processo;
import politicas.Politica;

public class MY extends Politica {

	public MY(Arquivo arquivo) {
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

	// M�todo para decidir qual processo deve ir para a troca de contexto
	private int escolheProcesso() {
		int processoEscolhido = 0;
		for (int i = 0; i < getProntos().size(); i++) {
			if (((getProntos().get(i).getCpu1() + getProntos().get(i).getCpu2()) / getProntos().get(i)
					.getEs()) < ((getProntos().get(processoEscolhido).getCpu1()
							+ getProntos().get(processoEscolhido).getCpu2())
							/ getProntos().get(processoEscolhido).getEs()))
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

	// Fun��o que escreve no arquivo de sa�da
	@Override
	public void escreveSaida() {
		// Chama m�todo para reordenar os processos de acordo com nome.
		reordena(getProcessos());

		try {
			BufferedWriter arquivo = new BufferedWriter(new FileWriter("out.txt", true));

			arquivo.write("MY");
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
