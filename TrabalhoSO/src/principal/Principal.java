package principal;

import objetos.Arquivo;
import politicas.curtoPrazo.FCFS;
import politicas.curtoPrazo.MY;
import politicas.curtoPrazo.Prioridades;
import politicas.curtoPrazo.RoundRobin;
import politicas.longoPrazo.SJF;

/**
 * @author Douglas Soares
 *
 */

public class Principal {

	
	public static void main(String[] args) {
		Arquivo arquivo = new Arquivo();
		new SJF().ordena(arquivo);

		FCFS fcfs = new FCFS(arquivo);
		fcfs.calcular();
		
		new SJF().ordena(arquivo);
		Prioridades prioridades = new Prioridades(arquivo);
		prioridades.calcular();
		
		new SJF().ordena(arquivo);
		RoundRobin rr = new RoundRobin(arquivo, 20);
		rr.calcular();
		
		new SJF().ordena(arquivo);
		MY my = new MY(arquivo);
		my.calcular();
		
		System.out.println("Simulação concluída com sucesso!");
	}
	
}
