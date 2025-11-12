package projeto;

//imports necessários para o desenvolvimento da classe Amostra
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Amostra implements Serializable {
	private static final long serialVersionUID = 1L;
	
	ArrayList<int []> raw_data= new ArrayList<int []>(); 
	int[] Domain_List;
	boolean balance;
	int Class_data;
	
	String line = "";
	
	//função que permite criar uma amostra a partir de um ficheiro .csv
	public Amostra(String csvFile) {
		try(BufferedReader br = new BufferedReader(new FileReader(csvFile))){
			while((line = br.readLine()) != null) {
				String[] dataStr = line.split(","); 
				int[] dataVec = new int[dataStr.length];
				for(int i = 0; i < dataStr.length; i++) {
					dataVec[i] = Integer.parseInt(dataStr[i]);
				}
				this.add(dataVec);
			}
			this.Class_data = this.element(0).length; //número de variáveis da amostra(classe incluída)
			this.Domain_List = new int[this.Class_data]; 
			this.Domains();
			balance = this.Domain_List[Class_data-1] == 2;
			if(balance) this.dataBalancing(); //efetua-se databalancing automático, se a classe da amostra for binária
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//função auxiliar que permite remover um dado vetor da amostra 
	public int [] remove_p(int p) {
		int[] res = this.element(p);
		raw_data.remove(p);
		return res;
	}
	
	//função auxiliar que permite adicionar um dado vetor à amostra 
	public void add_p(int p, int[] vect) {
		if(raw_data.size() == 0) raw_data.add(vect);
		else if(vect.length == raw_data.get(0).length)raw_data.add(p, vect);
		
	}
	
	//função auxiliar da função Count, que devolve uma lista (int[]) com os valores presentes num 
	//vetor da amostra nas várias posições pedidas pelo vetor de variáveis
	public int[] Helper(int[] variables, int[] data) {
		int[] temp = new int[variables.length];
		for(int var = 0; var < variables.length; var++) {
			temp[var] = data[variables[var]];
		}
		return temp;
	}
	
	//função auxiliar da função Domain que guarda todos os domínios de todas as variáveis da amostra numa lista
	public void Domains() {
		for(int variable = 0; variable < Class_data; variable++) {
			for(int[] vect: raw_data) {
				if(vect[variable] >= Domain_List[variable]) Domain_List[variable]=vect[variable]+1;
			}
		}
	}
	
	//função auxiliar que efetua o shuffling da amostra
	public void Shuffle () {
		Collections.shuffle(this.raw_data);
	}
	
	//getter e setters da amostra
	public ArrayList<int[]> getRaw_data() {
		return raw_data;
	}
	
	public void setRaw_data(ArrayList<int[]> raw_data) {
		this.raw_data = raw_data;
		this.Class_data = this.element(0).length;
		this.Domain_List = new int[this.Class_data];
		this.Domains();
	}

	public boolean isBalance() {
		return balance;
	}

	public int getClass_data() {
		return Class_data;
	}

	//função principal que recebe um vetor e acrescenta-o à amostra
	public void add(int[] vect) {
		if(raw_data.size() == 0) raw_data.add(vect);
		else if(vect.length == raw_data.get(0).length) raw_data.add(vect);
		else throw new RuntimeException("Vector introduced is not valid!");
	}
	
	//função principal que retorna o comprimento da amostra
	public int length() {
		return raw_data.size();
	}
	
	//função principal que recebe uma posição e retorna o número de elementos possíveis da variável dessa posição (domínio)
	public int Domain(int variable) {
		return Domain_List[variable];
	}
	
	//função principal que recebe uma posição e retorna o vetor da amostra
	public int[] element(int vect_pos) {
		return raw_data.get(vect_pos);
	}
	public String element_prt(int vect_pos) {
		return Arrays.toString(element(vect_pos));
	}
	
	//função principal que recebe um vetor de variáveis e um vetor de valores e retorna o número de 
	//ocorrências desses valores para essas variáveis na amostra
	public int Count(int[] variables, int[] values) {
		if (variables.length==values.length) {
			//recorrendo à função auxiliar, Helper, efetua-se uma comparação das listas obtidas com o vetor de valores introduzido
			//para todos os vetores da amostra
			return (int) raw_data.stream().filter(vectors -> Arrays.equals(values, Helper(variables,vectors))).count();
		}
		else {
			throw new RuntimeException("Variables and values vectors must have the same length!");
		}
	}
	
	//função adicional que efetua o balanceamento dos dados, no caso de amostras com classes binárias
	public void dataBalancing() {
		int[] position = {Class_data-1}; 
	 	int[] zero = {0};
		int[] one = {1};
		double b = this.Count(position,zero);
		double c = this.Count(position,one);
		ArrayList<int[]> aux_List = new ArrayList<int[]>();
		if(b < c && (c-b)/b >= 0.5) {//adiciona vetores com classe 0 a uma lista auxiliar (aux_List)
			int repetitions = 0;
			while(repetitions < Math.round((c-b)/b)) {
				raw_data.stream().filter(vector->vector[this.Class_data-1] == 0).forEach(vector -> aux_List.add(vector));
				repetitions++;
			}
		}
		if(c < b && (b-c)/c >= 0.5) {//adiciona vetores com classe 1 a uma lista auxiliar (aux_List)
			int repetitions = 0;
			while(repetitions < Math.round((b-c)/c)) { 
				raw_data.stream().filter(vector->vector[this.Class_data-1] == 1).forEach(vector -> aux_List.add(vector));
				repetitions++;
			}
		}
		raw_data.addAll(aux_List);//adiciona a aux_List à amostra
	}
	
	@Override
	public String toString() {
		String Str = "Sample: \n";
		for (int i=0; i < raw_data.size(); i++) {
			Str += Arrays.toString(raw_data.get(i));
			Str +="\n";
		}
		return Str;
	}
	public static void main(String[] args) {
		Amostra A = new Amostra("iris.csv");
	}
}
