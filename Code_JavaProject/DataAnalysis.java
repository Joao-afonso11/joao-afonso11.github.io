package projeto;

//import das funções necessárias para o desenvolvimento da classe DataAnalysis

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class DataAnalysis implements Serializable {
	private static final long serialVersionUID = 1L;
	//inicialização de variáveis
	Amostra raw_data;
	Grafoo grafo;
	Mutual_Information mi;
	File tempFile = new File("tempFile.csv");
	double[] score;
	int TP = 0;
	int TN = 0;
	int FP = 0;
	int FN = 0;
	int Co = 0;
	int In = 0;
	int classe;
	int k;
	int ksize;
	
	//método construtor que devolve a performance da rede de bayes, criada através de um dado dataset
	//com base no método KFoldCrossValidation ou LeaveOneOut
	public DataAnalysis(Amostra data, int c, double S) {
	this.raw_data = data;
	this.classe = raw_data.getClass_data();
	this.k = raw_data.length();
	if(c > 1 && c < k) this.k = c;
	this.ksize = raw_data.length()/k;
	if(c != 0)this.score = this.KFoldCrossValidation_LeaveOneOut(k,S);
	
	}
	
	//setters da classe
	public double[] getTest() {
		return score;
	}
	
	public double[] KFoldCrossValidation_LeaveOneOut(int c, double S){
	 
		ArrayList<int[]> listB = new ArrayList<int[]>();
		//for loop que permite a divisão da Amostra(raw_data) em k partes 
		//e a criação de uma Rede de Bayes para cada uma das partes com base nas outras k-1 partes 
		for(int section = 0; section <= k-1; section++) {
			int last;
			int first = section*ksize;
			if(section == k-1)last = raw_data.length();
			else  last = (section + 1) * ksize;
			//remoção de todos os vetores pertencentes a uma parte da Amostra e adição desses mesmos vetores a uma listB
			//estes vetores serão classificados pela Rede de Bayes criada através da parte não removida da Amostra
			for(int j = first; j<last; j++){
				int[] stand_in = raw_data.remove_p(first);
				listB.add(stand_in);
				}
			//inicialização dos objetos necessários para criar cada Rede de Bayes através da "raw_data".
			Mutual_Information M = new Mutual_Information(raw_data);
			Grafoo G = new Grafoo(raw_data.getClass_data());
			G.populate_grafoo(M);
			Floresta F = G.max_spanning_tree();
			RedeBayes temp_rede = new RedeBayes(F,raw_data,S); 
			//incrementar variáveis pré-definidas com base na classificação dos vetores da listB.
			for(int[] vector: listB) {int[] x = Arrays.copyOfRange(vector, 0, classe-1);
			int p = temp_rede.classifier(x);//classificação do vetor pela Rede de Bayes
			int q = vector[classe-1];//classifação do vetor definida no dataset
			if(p == 1 && q == 1)
				TP += 1;//Verdadeiros Positivos 
			if(p == 0 && q == 0)
				TN+=1;//Verdadeiros Negativos 
			if(p == 1 && q == 0)
				FP += 1;//Falsos Positvos
			if(p == 0 && q == 1)
				FN += 1;//Falsos Negativos
			if(p == q) Co++;//Classficações Corretas
			if(p != q) In++;//Classificações Incorretas
			};
			//adiçiona de volta, na posição correta, os vetores previamente retirados da Amostra, limpando a listB simultaneamente.
				for(int j = first; j < last; j++){
			raw_data.add_p(j, listB.get(0));
			listB.remove(0);
				}
			//imprime a percentagem completa do DataAnalysis a cada momento
			//note-se que estas linhas, pode ser comentadas para tornar o código um pouco mais eficiente
//			double scale1 = Math.pow(10, 5);
//			double aux = ((section+1)*ksize);
//			double percent = Math.round(aux/raw_data.length()*scale1) / scale1*100;
//			System.out.println(percent+" % . . . ");
			}
		//retorna os valores de accuracy,precision, recall, f1_score e specificity se o dataset tiver uma classe binária 
		//ou apenas o valor de accuracy caso contrário.
		if(raw_data.isBalance()) { 
			double aux1 = TP+TN;
			double aux2 = TN+TP+FN+FP;
			double aux3 = TP+FP;
			double aux4 = TP+FN;
			double aux5 = TN+FP;
			double accuracy = aux1/aux2 ;
			double precision = TP/aux3;
			double recall = TP/aux4;
			double f1_score = 2*(recall*precision)/(recall+precision) ;
			double specificity = TN/aux5;
			double [] Scores = {accuracy,precision,recall,f1_score,specificity};
			return Scores;
		}
		else {
			double aux6 = Co+In;
			double accuracy = Co/aux6;
			double [] Scores = {accuracy};
			return Scores;
		}	
	}
	
	@Override
	public String toString() {
		String str = " DataAnalysis of the selected sample: \n";
		double scale = Math.pow(10, 4);
		if(raw_data.isBalance()) {
			str+="  - Accuracy: "+ Math.round( score[0]* scale) / scale*100+" % \n";
			str+="  - Precision: "+Math.round( score[1]* scale) / scale*100+" % \n";
			str+="  - Recall: "+Math.round( score[2]* scale) / scale*100+" % \n";
			str+="  - f1_score: "+Math.round( score[3]* scale) / scale*100+" % \n";
			str+="  - Specificity: "+Math.round( score[4]* scale) / scale*100+" % \n";
		}
		else {
			str+="  Accuracy: "+Math.round( score[0]* scale) / scale*100+" % \n";
		}
		return str;
	}
	
	public static void main(String[] args) {
		Amostra A = new Amostra("iris.csv");
		DataAnalysis D = new DataAnalysis(A,1,0.5);
	}
}