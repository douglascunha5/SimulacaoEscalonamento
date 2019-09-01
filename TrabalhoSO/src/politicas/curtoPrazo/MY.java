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

	// Método para decidir qual processo deve ir para a troca de contexto
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

	// Função que escreve no arquivo de saída
	@Override
	public void escreveSaida() {
		// Chama método para reordenar os processos de acordo com nome.
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
