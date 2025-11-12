package projeto;

//imports necessários para o desenvolvimento da classe Floresta
import java.io.Serializable;
import java.util.Arrays;

public class Floresta implements Serializable {
	private static final long serialVersionUID = 1L;

	//a floresta foi idealizada como uma lista (int[]) em que apenas se indica o pai de cada nó/posição;
	//deve-se também ter em conta que uma floresta consiste num conjunto de árvores isoladas 
	//(em que cada nó tem um só pai)
	int[] Parents;

	//getters da Floresta
	public int getFather(int Son) {
		return Parents[Son];
	}
	
	public int[] getAncestors() {
		return Parents;
	}
	
	//função auxiliar que devolve o pai primordial de um nó, antes de este ligar à raiz
	//esta funçaõ ajuda a garantir que a árvore não tem ciclos
	public int GrandFather(int Father) {
		while(Parents[Father] != -1) Father = Parents[Father]; 
		return Father;
	}

	//método construtor que recebe um natural n e retorna uma floresta com n nós e sem arestas
	public Floresta(int size) {
		Parents = new int[size];
		//quando ainda não existem arestas, todos os nós são raizes (valor -1 indica uma raiz)
		for(int Fathers = 0; Fathers < size; Fathers++) Parents[Fathers] = -1; 
	}
	
	//função principal que recebe 2 nós (n-son e m-father) e torna o pai de n, o nó m
	public void set_Parent(int Son, int Father) {
		if(Father != Son && GrandFather(Father) != Son) Parents[Son] = Father;
		else throw new RuntimeException("Invalid combination of son and father!");
	}
	
	//função principal que retorna verdadeiro se e só se a floresta é uma árvore 
	public boolean treeQ() {
		//verifica-se que apenas existe uma só raiz
		return Arrays.stream(Parents).filter(Fathers -> Fathers==-1).count()==1;
	}
	
	@Override
	public String toString() {
		return "Forest: \n"+ Arrays.toString(Parents);
	}
	
	public static void main(String args[]) {
		Floresta F = new Floresta(20);
	}
}
