package projeto;

//import das funções necessárias para desenvolvimento da classe da RedeBayes

import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;
import java.lang.Math;

class RedeBayes implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Floresta forest;
	ArrayList<ArrayList<ArrayList<Double>>> matrix;
	Double S;
	Grafoo pesos;
	Amostra data;
	ArrayList<ArrayList<Double>> probability_mtx;
	
	//método construtor BN  que recebe uma árvore cuja raiz é a classe, um conjunto de
	//dados e um double S e constrói a rede de Bayes com a estrutura da árvore e com
	//as distribuições DFO's amortizadas com pseudo-contagens S
	public RedeBayes(Floresta arvore, Amostra dados, Double s) {
		S = s;
		this.data = dados;
		this.forest = arvore;
		this.matrix = this.main_cycle();
	}
	//getters and setters
	public Floresta getForest() {
		return forest;
	}

	//função auxiliar que efetua a operação do cálculo de uma DFO amortizada com pseudo-contagens S
	public Double operation(int Td, int Tw, int D) {
		return (Math.abs(Td) + S) / (Math.abs(Tw) + (S * Math.abs(D)));
	}	

	//função auxiliar que calcula o valor de Td para uma variável em específico
	//valor de di e wi definidos
	public int ret_Td(int pos_F, int val_F, int val_P) {
		int[] List = {pos_F,forest.getFather(pos_F)};
		int[] vect = {val_F,val_P};
		return data.Count(List,vect);
	}
	
	//função auxiliar que calcula o valor de Tw para uma variável em específico
	//valor de wi definidos
	public int ret_Tw(int pos_F, int val_P) {
		int[] List = {forest.getFather(pos_F)};
		int[] vect = {val_P};
		return data.Count(List,vect);
	}
	
	//função auxiliar que percorre o domínio do pai de uma variável Xi
	//calculando os DFO's correspondentes
	public ArrayList<Double> cycle_fixed_valF(int pos_F, int val_F, int Dom_F) {
		int pos_P = forest.getFather(pos_F);
		ArrayList<Double> result = new ArrayList<Double>();	
		if(pos_P!=-1) {	
			int Domain_P = data.Domain(pos_P);
			for(int val_p=0; val_p<Domain_P; val_p++) {
				int Td_aux = this.ret_Td(pos_F, val_F, val_p);
				int Tw_aux = this.ret_Tw(pos_F, val_p);
				result.add(this.operation(Td_aux, Tw_aux, Dom_F));
			}
		}
		//no caso da raiz, Td corresponde à frequência de cada classe
		//e Tw ao comprimento da amostra
		else {
			int [] posF = {pos_F};
			int [] valF = {val_F};
			int Td_aux2 = data.Count(posF, valF);
			int Tw_aux1 = data.length();
			result.add(this.operation(Td_aux2, Tw_aux1, Dom_F));
		}
		return result;	
	}
	
	//função auxiliar que percorre o domínio de uma variável Xi
	//criando assim uma matriz de valores de DFO's (linhas - valores de di;
	//colunas - valores de wi) por variável
	public ArrayList<ArrayList<Double>> cycle_fixed_Node(int pos_F){
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		int Domain_F = data.Domain(pos_F);
		for(int val_F = 0; val_F < Domain_F; val_F++) {
			result.add(this.cycle_fixed_valF(pos_F, val_F, Domain_F));
		}
		return result;	
	}
	
	//função auxiliar que percorre as variáveis todas da amostra 
	//devolvendo uma matriz de matrizes com todas as probabilidades possíveis para a amostra de input
	public ArrayList<ArrayList<ArrayList<Double>>> main_cycle(){
		ArrayList<ArrayList<ArrayList<Double>>> result = new ArrayList<ArrayList<ArrayList<Double>>>();
		for(int pos_F = 0; pos_F < forest.getAncestors().length; pos_F++) {
			result.add(this.cycle_fixed_Node(pos_F));
		}
		return result;
	}

	@Override
	public String toString() {
        String Str = "Rede Bayes \n";
        Str += "---------- \n";
        Str += "Floresta = "+forest+"\n";
        Str += "Matriz = "+Arrays.toString(matrix.toArray());
        return Str;
    }
	
	//função auxiliar que recebe um vetor e um nó da árvore, devolvendo a probabilidade 
	//a partir da matriz de probabilidades resultante do main_cycle
	public Double prob_Vect(int[] vect, int node) {
		if(forest.getFather(node) == -1) {
			int Di = vect[node];
			Double res;
			res = (double) matrix.get(node).get(Di).get(0);
			return res;
		}
		else {
			int pos_P = forest.getFather(node);
			int Di = vect[node];
			int Wi = vect[pos_P];
			return matrix.get(node).get(Di).get(Wi);
		}
	}
	
	//função auxiliar que recebe um vetor e devolve uma lista (ArrayList) com as probabilidades para cada uma
	//das posições do mesmo
	public ArrayList<Double> prob_cycle(int[] vect) {
		ArrayList<Double> result = new ArrayList<Double>();
		for(int node = 0; node < forest.getAncestors().length; node++){
			result.add(prob_Vect(vect,node));
		}
		return result;
	}
	
	//função auxiliar que devolve uma lista (ArrayList) com os domínios de cada uma das variáveis da amostra
	public ArrayList<Integer> Domain_aus() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int node = 0; node < forest.getAncestors().length; node++) {
			res.add(data.Domain(node));			
		}
		return res;
	}
	
	//função auxiliar que, usando a função acima, verifica se o vetor introduzido para análise é válido
	public boolean Is_Valid(int[] vect) {
		boolean flag = true;
		ArrayList<Integer> D = Domain_aus(); 
		for (int node=0; node<vect.length; node++) {
			if (D.get(node) <= vect[node]) {
				flag=false;
				break;
			}
		}
		return flag;
	}
	
	//função principal que recebe um vetor e utilizando a Rede de Bayes, calcula a sua probabilidade
	//(produto de todos os valores da lista (ArrayList) resultante da função auxiliar prob_cycle)
	public Double prob_value(int[] v) {
		if (Is_Valid(v)) {
			ArrayList<Double> t = prob_cycle(v);
			Double res = 1.0; //elemento neutro da multiplicação
			for (int node = 0; node < t.size(); node++) {
				res = res*(t.get(node));
			}
			return res;
		}
		else {
			throw new RuntimeException("Vetor introduzido é inválido");//mensagem de erro
		}
	}
	
	//função auxiliar que devolve uma lista (ArrayList) com as probabilidades de um dado vetor alterado 
	//(comprimento de um vetor da amostra - 1) com todas as classes de um dataset
	public ArrayList<Double> classifier_aux(int[] v_altered) {
		if (v_altered.length == forest.getAncestors().length-1) {
			ArrayList<Double> test = new ArrayList<Double>();
			int classe1 = data.element(0).length-1;
			for(int i = 0; i < data.Domain(classe1); i++) {
				int[] v_teste = new int[v_altered.length+1];
				for(int j = 0; j < v_teste.length; j++) {
					if (j == v_teste.length-1) {
						v_teste[j] = i;
					}
					else {
						v_teste[j] = v_altered[j];
					}
				}
				test.add(prob_value(v_teste));
			}
			return test;
		}
		else {
			throw new RuntimeException("Vetor introduzido é inválido");//mensagem de erro
		}
	}
	
	//função que devolve a classe correspondente à maior probabilidade calculada pela função auxiliar acima
	public int classifier(int[] v_altered) {
		Double max = 0.0;
		int classe = 0;
		ArrayList<Double> res_test = classifier_aux(v_altered);
		for (int i = 0; i < res_test.size(); i++) {
			if (res_test.get(i) >= max) {
				max = res_test.get(i);
				classe = i;
			}
		}
		return classe;
	}
	
	public static void main (String Args[]) {
		Amostra A = new Amostra("bcancer.csv");
		Mutual_Information M = new Mutual_Information(A);
		Grafoo G = new Grafoo(A.getClass_data());
		G.populate_grafoo(M);
		Floresta F = G.max_spanning_tree();
		RedeBayes R = new RedeBayes(F,A,0.5);
	}	
}