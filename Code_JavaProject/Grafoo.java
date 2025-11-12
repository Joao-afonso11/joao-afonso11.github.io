package projeto;

//import das funções necessárias ao desenvolvimento da classe Grafoo
import java.io.Serializable;
import java.util.Arrays;

class Grafoo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int dim;
	double Nodes[][];
	
	//método construtor que devolve um grafo com uma dimensão(dim) contendo nós sem arestas
	//o grafo pesado é definido como uma matriz de adjacência com os pesos das ligações entre os nós
	//iniciados a 0.0
	public Grafoo(int dim) {	
		this.dim = dim;
		this.Nodes = new double [dim][dim];
		Arrays.stream(Nodes).forEach(list -> Arrays.fill(list, 0.0));
	}
	
	//função principal que recebe dois nós e um peso, adicionando ao grafo uma aresta entre esses dois nós 
	//com o peso indicado
	public void add_edge(int Nda, int Ndb, double Weight) { 
		if (Nda != Ndb && Nodes[Nda][Ndb] == 0.0) {
			//como o grafo não é direcionado, a matriz é simétrica com diagonal 0
			Nodes[Nda][Ndb] = Weight; 
			Nodes[Ndb][Nda] = Weight;
		}
	}

	//função auxiliar que recebe um dado nó e uma lista de booleanos de nós visitados e, por sua vez,
	//devolve o próximo nó a que se deve ligar (ligação com maior peso)
	public int max_Node(int current_Node, boolean[] Visited_Nodes) {
		double max = -1.0; 
		int pos = -1;
		for (int i = 0; i < Nodes[current_Node].length; i++) {
			if (Nodes[current_Node][i] >= max && !Visited_Nodes[i] && current_Node != i) {
				max = Nodes[current_Node][i];
				pos = i;
			}
		}
		return pos;
	}
	
	//método auxiliar que verifica dentro de uma lista de booleanos se todos os valores são true
	public static boolean areAllTrue(boolean[] array) {
	    for(boolean b : array) if(!b) return false;
	    return true;
	}
	
	//função auxiliar que aplica o algoritmo Prim e devolve a floresta de extensão maximal
	public Floresta max_spanning_tree() { 
		//cria uma lista de nós visitados, a árvore de output final e o peso global da árvore
		//note-se que, neste caso, começa-se a construir a árvore a partir da raiz
		boolean [] seen = new boolean [dim];
		int [] Tree = new int [dim-1]; 
		double WeightData = 0.0;
		seen[dim-1]=true; //define o primeiro nó como visto (raiz da árvore)
		boolean done = false;
		Floresta pop_Tree = new Floresta(dim);
		//o ciclo percorre até todos os nós terem sido visitados
		while (!areAllTrue(seen) && !done) {
			//cria-se um valor de peso teste, que se inicializa com o mesmo peso da árvore global
			double TestWeights = WeightData;
			//cria-se uma ligação inexistente
			//(nó visitado e próximo nó, ambos inicializados com -1)
			int [] node_pos = {-1,-1};
			for(int i = 0; i < dim; i++) {
				if(seen[i]) {
					int auxNode = max_Node(i,seen);
					double auxWeight = Nodes[i][auxNode];
					//apenas se atualiza a ligação se o peso da árvore no momento + o pesoa da ligação
					//é maior que o peso teste, que se vai atualizando 
					if(WeightData+auxWeight > TestWeights) {
						TestWeights = WeightData + auxWeight;
						node_pos[0] = i;
						node_pos[1] = auxNode;
					}
				}
			}
			//efetua-se a ligação forçada dos nós sem ligações à raiz
			if(TestWeights == WeightData) {
				for(int position = 0; position < dim; position++) {
					if(!seen[position])pop_Tree.set_Parent(position,dim-1);
				}
				done = true;
			}
			else {
				WeightData=TestWeights;
				seen[node_pos[1]] = true;
				Tree[node_pos[1]] = node_pos[0]+1;
				pop_Tree.set_Parent(node_pos[1],node_pos[0]);
			}
			
		}
		return pop_Tree;
	}
	
	//função auxiliar que preenche o grafo com os pesos calculados a partir a classe auxiliar
	//Mutual_Information
	public void populate_grafoo(Mutual_Information pesos) {
		for (int i=0; i<dim; i++) {
			for (int j=i+1; j<dim; j++) {
				this.add_edge(i, j, pesos.weight(i, j));
			}
		}		
	}

	@Override
	public String toString() {
        String Str= "Tree :";
        for (int i=0; i <dim; i++) {
        	Str+="\n"+"\n	Node "+i+" conected to:";
        	for(int j=0; j<dim; j++) {
	            if(Nodes[i][j] !=0.0) {
	            	Str+="\n Node "+j+" with weight of " + Nodes[i][j] ;
	            }
        	}        
        }   return Str;
	}
	
	public static void main(String args[]) {
		Grafoo G = new Grafoo(11);
		Amostra A = new Amostra("bcancer.csv");
		Mutual_Information B0 = new Mutual_Information(A);
		G.populate_grafoo(B0);

	}
}