package projeto;

//imports necessários para o desenvolvimento da classe auxiliar Mutual_Information 
//(para o cálculo da informação mútua condicional)
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Mutual_Information implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Amostra base_data;
	int data_size;
	int[] data_domains;
	
	//a classe recebe uma amostra
	public Mutual_Information(Amostra base_data) {
		this.base_data = base_data;
		this.data_size = base_data.length();
	}

	//função auxiliar que efetua o cálculo de um só termo do somatório da informação mútua condicional
	public double aux_weight(int[] n1, int[] n2, int[] n3, int i, int j) {
	    double res = 0.0;
	    int[] i1 = {i}; int[] j1 = {j}; int[] ij = {i,j};
		double P1 = (double) base_data.Count(n3, ij);
		double P2 = (double) base_data.Count(n1, i1);
		double P3 = (double) base_data.Count(n2, j1);
		if (P1!=0.0) {
				res = ((P1 / (double) data_size) * (Math.log((P1 / (double) data_size) / ((P2 * P3) / ((double) Math.pow(data_size, 2)))) ));
		}
		return res;
	}
	
	//função auxiliar que efetua o cálculo da informação mútua condicional entre dois nós ligados
	public double weight(int n1, int n2) {
		int[] n2i = {n2};
		int[] n1i = {n1};
		int[] n1n2 = {n1,n2};
		int[] n2_dom = new int[base_data.Domain(n2)];
		IntStream.iterate(0, i -> i + 1).limit(n2_dom.length).forEach(i -> n2_dom[i] = i);
		int[] n1_dom = new int[base_data.Domain(n1)];
		IntStream.iterate(0, i -> i + 1).limit(n1_dom.length).forEach(i -> n1_dom[i] = i);
		return Arrays.stream(n1_dom).mapToDouble(position1 -> Arrays.stream(n2_dom).mapToDouble(position2 -> aux_weight(n1i,n2i,n1n2,position1,position2)).sum()).sum();
	}
	
	public static void main(String args[]) {
		Amostra A = new Amostra("thyroid.csv");
		Mutual_Information B0 = new Mutual_Information(A);
	}
}