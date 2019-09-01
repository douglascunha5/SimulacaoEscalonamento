package politicas.longoPrazo;


import objetos.Arquivo;
import objetos.Processo;

/**
 * @author Douglas Soares
 *
 */
public class SJF {
	//Método que irá ordenar o arquivo através de um bubble sort.
	public Arquivo ordena(Arquivo arquivo){
		Processo aux;
		
		for(int i = 0; i<arquivo.getProcessos().size(); i++){
			for(int j = 0; j<arquivo.getProcessos().size()-1; j++){
				if(arquivo.getProcessos().get(j).getCpu1() + arquivo.getProcessos().get(j).getCpu2() > arquivo.getProcessos().get(j+1).getCpu1()+arquivo.getProcessos().get(j+1).getCpu2()){
					aux = arquivo.getProcessos().get(j);
					arquivo.getProcessos().set(j, arquivo.getProcessos().get(j+1));
					arquivo.getProcessos().set(j+1, aux);
				}
			}
		}
		
		return arquivo;
	}
}
