package politicas.curtoPrazo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import objetos.Arquivo;
import objetos.Processo;
import politicas.Politica;

public class RoundRobin extends Politica {
	private int quantum;

	public RoundRobin(Arquivo arquivo, int quantum) {
		super(arquivo);
		this.quantum = quantum;
	}

	@Override
	public void usaCpu(Processo processo) {
		// Se o processo ainda n�o tiver feito ES ele executa a sua CPU1.
		if (!processo.isFezES()) {
			if (processo.getCpuUsada() + quantum < processo.getCpu1()) {
				setTempo(getTempo() + quantum);
				processo.setCpuUsada(processo.getCpuUsada() + quantum);
				processo.setTempoEspera(getTempo());

				// Verifica se algum processo sai da ES antes de um processo que est� na CPU
				// terminar.
				if (getEs().size() != 0) {
					int saemEs = saemES(); // Vari�vel que ir� receber quantos processos devem sair da ES.

					// Se algum processo sai da ES antes do processo que est� na cpu terminar
					// executa a fun��o saiES().
					if (saemEs > 0) {
						for (int i = 0; i < saemEs; i++) {
							saiES();
						}
						
					} else { // Sen�o o processo que est� na cpu retorna para a fila de prontos.
						if (getProntos().size() > 0) {
							//Ir� verifiar se algum processo sai da ES ao mesmo tempo que algu�m sai da CPU
							//Se sim, quem sai da ES vai pra fila de prontos primeiro
							if(getEs().size() > 0) {
								int menorEs = menorES();
								if(getEs().get(menorEs).getTempoEspera() + getEs().get(menorEs).getEs() <= getTempo()+getTcp()) {
									saiES();
								}
								else {
									getProntos().add(processo);
									setCpu(null);
									trocaContexto(getProntos().get(0));
								}
							}
							else {
								getProntos().add(processo);
								setCpu(null);
								trocaContexto(getProntos().get(0));
							}
							
						} else {
							usaCpu(processo);
						}
					}
				} else {
					if (getProntos().size() > 0) {
						//Ir� verifiar se algum processo sai da ES ao mesmo tempo que algu�m sai da CPU
						//Se sim, quem sai da ES vai pra fila de prontos primeiro
						if(getEs().size() > 0) {
							int menorEs = menorES();
							if(getEs().get(menorEs).getTempoEspera() + getEs().get(menorEs).getEs() <= getTempo()+getTcp()) {
								saiES();
							}
							else {
								getProntos().add(processo);
								setCpu(null);
								trocaContexto(getProntos().get(0));
							}
						}
						else {
							getProntos().add(processo);
							setCpu(null);
							trocaContexto(getProntos().get(0));
						}
					} else {
						usaCpu(processo);
					}
				}
			} else {
				setTempo(getTempo() + (processo.getCpu1() - processo.getCpuUsada()));
				processo.setCpuUsada(processo.getCpu1());
				processo.setTempoEspera(getTempo());

				// Verifica se algum processo sai da ES antes de um processo que est� na CPU
				// terminar.
				if (getEs().size() != 0) {
					int saemEs = saemES(); // Vari�vel que ir� receber quantos processos devem sair da ES.

					// Se algum processo sai da ES antes do processo que est� na cpu terminar
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
		}
		// Sen�o ele executa sua CPU2.
		else {
			if (processo.getCpuUsada() + quantum < processo.getCpu2()) {
				setTempo(getTempo() + quantum);
				processo.setCpuUsada(processo.getCpuUsada() + quantum);
				processo.setTempoEspera(getTempo());

				// Verifica se algum processo sai da ES antes de um processo que est� na CPU
				// terminar.
				if (getEs().size() != 0) {
					int saemEs = saemES(); // Vari�vel que ir� receber quantos processos devem sair da ES.

					// Se algum processo sai da ES antes do processo que est� na cpu terminar
					// executa a fun��o saiES().
					if (saemEs > 0) {
						for (int i = 0; i < saemEs; i++) {
							saiES();
						}
					} else { // Sen�o o processo que est� na cpu volta para a fila de prontos.
						if (getProntos().size() > 0) {
							//Ir� verifiar se algum processo sai da ES ao mesmo tempo que algu�m sai da CPU
							//Se sim, quem sai da ES vai pra fila de prontos primeiro
							if(getEs().size() > 0) {
								int menorEs = menorES();
								if(getEs().get(menorEs).getTempoEspera() + getEs().get(menorEs).getEs() <= getTempo()+getTcp()) {
									saiES();
								}
								else {
									getProntos().add(processo);
									setCpu(null);
									trocaContexto(getProntos().get(0));
								}
							}
							else {
								getProntos().add(processo);
								setCpu(null);
								trocaContexto(getProntos().get(0));
							}
						} else {
							usaCpu(processo);
						}
					}
				} else {
					if (getProntos().size() > 0) {
						//Ir� verifiar se algum processo sai da ES ao mesmo tempo que algu�m sai da CPU
						//Se sim, quem sai da ES vai pra fila de prontos primeiro
						if(getEs().size() > 0) {
							int menorEs = menorES();
							if(getEs().get(menorEs).getTempoEspera() + getEs().get(menorEs).getEs() <= getTempo()+getTcp()) {
								saiES();
							}
							else {
								getProntos().add(processo);
								setCpu(null);
								trocaContexto(getProntos().get(0));
							}
						}
						else {
							getProntos().add(processo);
							setCpu(null);
							trocaContexto(getProntos().get(0));
						}
					} else {
						usaCpu(processo);
					}
				}
			} else {
				setTempo(getTempo() + (processo.getCpu2() - processo.getCpuUsada()));
				processo.setCpuUsada(processo.getCpu2());
				processo.setTempoEspera(getTempo());

				// Verifica se algum processo sai da ES antes de um processo que est� na CPU
				// terminar.
				if (getEs().size() != 0) {
					int saemEs = saemES(); // Vari�vel que ir� receber quantos processos devem sair da ES.

					// Se algum processo sai da ES antes do processo que est� na cpu terminar
					// executa a fun��o saiES().
					if (saemEs > 0) {
						for (int i = 0; i < saemEs; i++) {
							saiES();
						}
					} else { // Sen�o o processo que est� na cpu � encerrado.
						encerraProcesso(processo);
					}
				} else {
					encerraProcesso(processo);
				}
			}
		}
	}
	
	@Override
	// M�todo que simula uma troca de contexto do processo.
	public void trocaContexto(Processo processo) {
		// Se a cpu n�o estiver ocupada o processo assume a cpu.
		if (getCpu() == null) {
			setTempo(getTempo() + getTcp());
			getProntos().remove(0);
			setCpu(processo);
			usaCpu(processo);
		}
		// Sen�o o processo que est� na cpu volta para a fila de espera e o pr�ximo na fila
		// assume a CPU.
		else {
			if(getCpu().getCpuUsada() == getCpu().getCpu1() && getCpu().isFezES() == false) {
				fazES(getCpu());
			}
			else if(getCpu().getCpuUsada() == getCpu().getCpu2() && getCpu().isFezES() == true) {
				encerraProcesso(getCpu());
			}
			else {
				setTempo(getTempo() + getTcp());
				getProntos().add(getCpu());
				getProntos().remove(0);
				setCpu(processo);
				usaCpu(processo);
			}
		}
	}

	@Override
	// M�todo que simula a entrada/saida de um processo.
	public void fazES(Processo processo) {
		getEs().add(processo);
		setCpu(null);
		processo.setCpuUsada(0);

		// Verifica se algum processo da fila de prontos ir� usar cpu por completo antes
		// de algum sair da ES.
		if (getProntos().size() != 0) {
			trocaContexto(getProntos().get(0));

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
		}
		else {
			getProntos().add(processo);
		}
	}

	// Fun��o que escreve no arquivo de sa�da
	@Override
	public void escreveSaida() {
		// Chama m�todo para reordenar os processos de acordo com nome.
		reordena(getProcessos());

		try {
			BufferedWriter arquivo = new BufferedWriter(new FileWriter("out.txt", true));

			arquivo.write("Round-Robin(20)");
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
