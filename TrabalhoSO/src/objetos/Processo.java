package objetos;

public class Processo {
	private int cpu1, es, cpu2, prioridade, tempoEspera, quandoEntrou, cpuUsada;
	private String nome;
	private boolean fezES;

	public Processo(String nome, int cpu1, int es, int cpu2, int prioridade) {
		this.cpu1 = cpu1;
		this.es = es;
		this.cpu2 = cpu2;
		this.prioridade = prioridade;
		this.nome = nome;
		this.tempoEspera =0;
		this.quandoEntrou = 0;
		this.cpuUsada = 0;
		this.fezES = false;
	}
	
	

	public int getCpu1() {
		return cpu1;
	}

	public void setCpu1(int cpu1) {
		this.cpu1 = cpu1;
	}

	public int getEs() {
		return es;
	}

	public void setEs(int es) {
		this.es = es;
	}

	public int getCpu2() {
		return cpu2;
	}

	public void setCpu2(int cpu2) {
		this.cpu2 = cpu2;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getTempoEspera() {
		return tempoEspera;
	}

	public void setTempoEspera(int tempoEspera) {
		this.tempoEspera = tempoEspera;
	}
	public boolean isFezES() {
		return fezES;
	}

	public void setFezES(boolean fezES) {
		this.fezES = fezES;
	}



	public int getQuandoEntrou() {
		return quandoEntrou;
	}



	public void setQuandoEntrou(int quandoEntrou) {
		this.quandoEntrou = quandoEntrou;
	}



	public int getCpuUsada() {
		return cpuUsada;
	}



	public void setCpuUsada(int cpuUsada) {
		this.cpuUsada = cpuUsada;
	}
	
	
	
}
