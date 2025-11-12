package projeto;

//import das funções necessárias ao desenvolvimento da Interface Gráfica 1

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import java.awt.SystemColor;

@SuppressWarnings("serial")
public class Interface_g1 extends JFrame {

	private JPanel contentPane;
	File local;
	DataAnalysis perf;
	Amostra A;
	Grafoo G;
	Mutual_Information MI;
	Floresta F;
	private JTextField textField_F;

	/**
	 * Launch the application.
	 */
	//função main que torna a interface visível ao utilizador
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface_g1 frame = new Interface_g1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Interface_g1() {
		//inicializa o painel onde a interface aparece
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 778, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		//Checkbox que permite dar ou não dar shuffle na amostra		
		JCheckBox checkbox_Shuffle = new JCheckBox("");
		checkbox_Shuffle.setBackground(new Color(0, 102, 164));
		checkbox_Shuffle.setBounds(397, 42, 21, 19);
		contentPane.add(checkbox_Shuffle);
		
		//Cria uma área de texto onde a amostra vai ser visualizada, com a opção de dar scroll
		JScrollPane scroll= new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 152, 339, 208);	
		contentPane.add(scroll);
		JTextArea textArea_Sample = new JTextArea();
		textArea_Sample.setLineWrap(true);
		scroll.setViewportView(textArea_Sample);
		
		//Campo de texto que recebe o nome do csv da amostra
		textField_F = new JTextField();
		textField_F.setBounds(10, 121, 322, 20);
		contentPane.add(textField_F);
		textField_F.setColumns(10);
		
		//Campo de texto que recebe o input do valor de S
		JTextField textField_S = new JTextField();
		textField_S.setBounds(465, 154, 51, 22);
		contentPane.add(textField_S);
		textField_S.setColumns(10);
		
		//Campo de texto que recebe o input do valor de K
		JTextField textField_K = new JTextField();
		textField_K.setBounds(624, 153, 51, 24);
		contentPane.add(textField_K);
		textField_K.setColumns(10);
		
		//Área de texto que permite visualizar os valores referentes à Data Analysis
		JTextArea textArea_Performance = new JTextArea();
		textArea_Performance.setBounds(456, 263, 230, 97);
		contentPane.add(textArea_Performance);
		
		//Caixa de seleção que permite escolher o tipo de análise a realizar 
		String[] Strings = {"Kfold","Leave one out","None"}; 
		JComboBox comboBox_analysis = new JComboBox(Strings);
		comboBox_analysis.setFont(new Font("Tahoma", Font.PLAIN, 9));
		this.setVisible(true);
		comboBox_analysis.setBounds(683, 79, 69, 19);
		contentPane.add(comboBox_analysis);
		
		
		JButton button_reset = new JButton("Reset");
		button_reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button_reset.setBackground(new Color(250, 12, 12));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button_reset.setBackground(null);
			}
		});
		button_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_Sample.setText("");
				textField_F.setText("");
				textField_S.setText("");
				textField_K.setText("");
				textArea_Performance.setText("");
			}
		});
		button_reset.setBounds(666, 11, 86, 38);
		contentPane.add(button_reset);	
		
		//Legendas de caixas de texto, caixa de seleção e áreas de texto 
		JLabel lbl = new JLabel("What type of analysis would you like to perform?");
		lbl.setBounds(392, 81, 283, 14);
		contentPane.add(lbl);
		
		JLabel lbl1 = new JLabel("Shuffle sample");
		lbl1.setBounds(424, 39, 86, 14);
		contentPane.add(lbl1);

		JLabel lbl2 = new JLabel("(recomended when doing Kfold)");
		lbl2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lbl2.setBounds(424, 53, 132, 14);
		contentPane.add(lbl2);
		
		JLabel lbl3 = new JLabel("Please insert the value of S ");
		lbl3.setFont(new Font("Tahoma", Font.BOLD, 9));
		lbl3.setBounds(417, 120, 139, 30);
		contentPane.add(lbl3);

		JLabel lbl4 = new JLabel("If you chose the Kfold method ");
		lbl4.setFont(new Font("Tahoma", Font.BOLD, 9));
		lbl4.setBounds(584, 122, 152, 14);
		contentPane.add(lbl4);
		
		JLabel lbl5 = new JLabel("please insert the value of K");
		lbl5.setFont(new Font("Tahoma", Font.BOLD, 9));
		lbl5.setBounds(584, 136, 147, 14);
		contentPane.add(lbl5);
		
		//Ação que permite mudar de cor butões
		JFileChooser filechooser = new JFileChooser(); 
		JButton button_read = new JButton("Read Sample");
		button_read.setFont(new Font("Tahoma", Font.PLAIN, 18));
		button_read.setBounds(10, 11, 141, 64);
		contentPane.add(button_read);
		button_read.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button_read.setBackground(new Color(130, 226, 245));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button_read.setBackground(null);
			}
		});
		button_read.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) {
				int r = filechooser.showOpenDialog((Component)e.getSource());//abre a caixa de procura para escolher a amostra
				if (r==filechooser.APPROVE_OPTION) {
					try {
					A = new Amostra(filechooser.getSelectedFile().getAbsolutePath()); //cria uma amostra com base no ficheiro csv selecionado
					G = new Grafoo(A.getClass_data());//cria um grafo com dimensão das variáveis da amostra
					MI = new Mutual_Information(A);//calcula as informações mútuas
					G.populate_grafoo(MI);//popula o grafo com base nas informações mútuas
					F = G.max_spanning_tree();//cria a max spanning tree do grafo populado
					//permite através da localização do ficheiro obter o nome csv do mesmo e imprimi-lo no campo de texto F
					local = filechooser.getSelectedFile();
					String [] test = local.getAbsolutePath().split("\\\\");
					String name = test[test.length-1];
					textArea_Sample.setText(A.toString());
					textField_F.setText("	Selected file: " + name);
					}
					catch(Exception ex) {
						textField_F.setText("");
						textArea_Sample.setText(" File data not compatible! \n Please introduce csv file!");
					}
				}
			}
		});

		
		JFileChooser fileChooser2 = new JFileChooser();		
		JButton button_Convert_Save = new JButton("Save Tree");
		button_Convert_Save.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) {
				int r = fileChooser2.showSaveDialog((Component)e.getSource());//abre a caixa de procura para escolher onde guardar o ficheiro .txt
				if (r==fileChooser2.APPROVE_OPTION ) {
					try {
						double S = Double.valueOf(textField_S.getText());//consulta o valor de S introduzido pelo utilizador
						RedeBayes raw_data = new RedeBayes(F, A, S);//cria uma rede de Bayes com a max spanning tree, a amostra e o double s
						//cria um ficheiro onde a rede de bayes vai ser guardada
						FileOutputStream fileOut = new FileOutputStream(fileChooser2.getSelectedFile().getAbsolutePath());
						ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
						if(checkbox_Shuffle.isSelected()) A.Shuffle();//Realiza shuffle na amostra caso a caixa de seleção encontre-se selecionada
						String option = comboBox_analysis.getSelectedItem().toString();//consulta a seleção escolhida pelo utilizador
							if(option == "Kfold") {//caso a seleção tenha sido Kfold
								try {//realiza uma análise do tipo KfoldCrossValidation
									int k = Integer.parseInt(textField_K.getText());//consulta o valor de k introduzido pelo utilizador
									perf= new DataAnalysis(A,k,S);
									textArea_Performance.setText(perf.toString());//imprime a Data Analysis referente à amostra A,
									//com os respetivos valores de k e S
															
								}
								catch(Exception ex) {//Erro interno do Kfold caso o k não seja válido
									textArea_Performance.setText("Data Analysis not performed: \n K not introduced ; \n or \n K isn't a natural number.");
									}				
								}
								if(option == "Leave one out") {//caso a seleção tenha sido Leave one out
									perf= new DataAnalysis(A,1,S);
									textArea_Performance.setText(perf.toString());//imprime a Data Analysis referente à amostra A,
									//com o respetivo valor de S
								}
								objectOut.writeObject(raw_data);//escreve a rede de Bayes no ficheiro
								//permite fechar o ficheiro
								objectOut.close();
								fileOut.close();
								textArea_Sample.setText("Tree Saved!");
					}
					catch(Exception ex) {
						textArea_Performance.setText("S not introduced!");
					}
				}
			}
		});
		button_Convert_Save.setBounds(492, 203, 164, 30);
		contentPane.add(button_Convert_Save);
		
		//Permite adicionar um fundo na interface
		JLabel background = new JLabel("");
		background.setIcon(new ImageIcon("src\\projeto\\background1.png"));
		background.setBounds(0,0,778,513);
		contentPane.add(background);
		
	}	
}