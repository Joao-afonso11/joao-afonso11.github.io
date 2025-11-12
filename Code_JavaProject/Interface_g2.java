package projeto;

//import das funções necessárias ao desenvolvimento da Interface Gráfica 2

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;

@SuppressWarnings("serial")
public class Interface_g2 extends JFrame {

	RedeBayes rede;
	private JPanel contentPane;
	private JTextField textField_Open;
	private JTextField textField_result;
	private JTextField textField_input;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface_g2 frame = new Interface_g2();
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
	public Interface_g2() {
		
		//inicializa o painel onde a interface aparece
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 778, 513);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));	
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//campo de texto que recebe o nome do ficheiro .txt aberto
		textField_Open = new JTextField();
		textField_Open.setBorder(null);
		textField_Open.setBounds(28, 72, 217, 35);
		contentPane.add(textField_Open);
		textField_Open.setColumns(10);
		
		//área de texto que permite visualizar os valores introduzidos para diagnóstico
		JTextArea textArea_Dia = new JTextArea();
		textArea_Dia.setLineWrap(true);
		textArea_Dia.setBounds(379, 213, 337, 53);
		contentPane.add(textArea_Dia);
		
		//campo de texto que devolve o resultado do diagnóstico
		textField_result = new JTextField();
		textField_result.setBounds(461, 342, 178, 35);
		contentPane.add(textField_result);
		textField_result.setColumns(10);
		
		//campo de texto que apresenta a quantidade de valores inserir para o diagnóstico
		textField_input = new JTextField();
		textField_input.setBorder(null);
		textField_input.setBounds(379, 169, 245, 20);
		contentPane.add(textField_input);
		textField_input.setColumns(10);
			
		//permite eliminar todos os campos de texto da inteface
		JButton button_reset = new JButton("Reset");
		button_reset.setBounds(678, 11, 74, 53);
		contentPane.add(button_reset);
		button_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_Dia.setText("");
				textField_Open.setText("");
				textField_input.setText("");
				textField_result.setText("");
			}
		});

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
		//Botão que permite abrir a rede de Bayes guardada na interface 1
		JFileChooser filechooser = new JFileChooser(); 
		JButton button_open = new JButton("Open Bayesian Tree");
		button_open.setBounds(28, 11, 149, 30);
		contentPane.add(button_open);
		button_open.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button_open.setBackground(new Color(130, 226, 245));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button_open.setBackground(null);
			}
		});
		button_open.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) {
				int r = filechooser.showOpenDialog((Component)e.getSource());//abre a caixa de procura para escolher que ficheiro .txt abrir
				if (r== filechooser.APPROVE_OPTION) {
					try {	
					String path = filechooser.getSelectedFile().getAbsolutePath();
					rede = ReadObjectFromFile(path); //Lê a rede de bayes inserida dentro do ficheiro .txt
					int dim = rede.getForest().getAncestors().length;//devolve o tamanho de um vetor 
					textField_input.setText("   Enter diagnosis with a length of " + (dim-1) + " :");//permite indicar ao utilizadar o tamanho do vetor a introduzir	
					String [] test = path.split("\\\\");
					String name = test[test.length-1];
					textField_Open.setText(" " + name + " tree is open!");
				}
					catch (Exception ex){
						textField_input.setText("");
						textField_Open.setText(" File data not compatible!");						
					}
				}
			}
		});
		
		//Botão que a partir da rede de Bayes classifica o vetor introduzido na interface
		JButton button_predict = new JButton("Predict");
		button_predict.setBounds(499, 290, 95, 30);
		contentPane.add(button_predict);
		
		//Legenda que permite definir o fundo da interface
		JLabel background = new JLabel("");
		background.setIcon(new ImageIcon("src\\projeto\\background1.png"));
		background.setBounds(0,0,778,513);
		contentPane.add(background);
		button_predict.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button_predict.setBackground(new Color(107, 250, 181));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button_predict.setBackground(null);
			}
		});
		button_predict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = textArea_Dia.getText();
				int[] rede_list = ConvListToInt_interface2(path);//converte a string num vetor de forma a ser lido pelo classificador
				try {//classifica a amostra
					textField_result.setText(" Sample classification: " + String.valueOf(rede.classifier(rede_list)));
				}
				catch (Exception ex){
					textField_result.setText(" Specified sample is invalid!");				
				}	
			}
		});
	}
		
	//Funções auxiliares dentro da interface que permitem ler a rede de bayes do ficheiro .txt(ReadObjectFromFile)
	//e converte a string introduzida pelo diagnóstico num vetor que pode ser classificado
	public static RedeBayes ReadObjectFromFile(String filepath) {
		try {
			FileInputStream fileIn = new FileInputStream(filepath);
	        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
	 
	        RedeBayes obj = (RedeBayes) objectIn.readObject();
	 
	        System.out.println("The Object has been read from the file");
	        objectIn.close();
	        return obj;
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}
	
	public static int[] ConvListToInt_interface2(String var) {
		String[] dataStr = var.split(",");
		int[] dataPos = new int[dataStr.length];
		for(int i=0; i<dataStr.length; i++) {	
			int value=Integer.parseInt(dataStr[i]);
			if (value>=0) {
				dataPos[i]=value;
			}
			else {
				throw new RuntimeException("Converted value is negative!"); 
			}
		}
		return dataPos;		
	}	
}