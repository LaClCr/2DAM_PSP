package clases;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


/**
 * Classe que representa la finestra d'ordres per fabricar peces.
 */

public class Order extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static final Font font = new Font("Consolas", Font.PLAIN, 14);
	private JTextField txtNombrePeces;
	private JTextField txtFitxer;
	private JTable tabla;
	private DefaultTableModel tableModel;
	private JButton btnEnviarOrdre;
	private JPanel centerPanel;
	private JLabel lblTriaForma;
	private JLabel lblTriaQuantitat;
	private JButton btnAfegir;
	private JCheckBox checkBox;
	private JLabel lblFitxer;
	private String[] tipusPeces = { "I", "O", "T", "J", "L", "S", "Z" };
	private JComboBox<String> cmbTipusPeces;
	private boolean outputTXT = false;
	private static List<String> comandes = new ArrayList<>();

	
	/**
     * Crea la finestra d'ordres.
     */
	public Order() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 365, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        centerPanel = new JPanel();
        contentPane.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(null);

        lblTriaForma = new JLabel("Tria el tipus de peça:");
        lblTriaForma.setBounds(10, 10, 184, 20);
        lblTriaForma.setFont(font);
        centerPanel.add(lblTriaForma);

        cmbTipusPeces = new JComboBox<>(tipusPeces);
        cmbTipusPeces.setBounds(220, 10, 110, 20);
        cmbTipusPeces.setFont(font);
        centerPanel.add(cmbTipusPeces);

        lblTriaQuantitat = new JLabel("Tria el nombre de peces:");
        lblTriaQuantitat.setBounds(10, 40, 200, 20);
        lblTriaQuantitat.setFont(font);
        centerPanel.add(lblTriaQuantitat);

        txtNombrePeces = new JTextField();
        txtNombrePeces.setBounds(220, 40, 110, 20);
        txtNombrePeces.setFont(font);
        centerPanel.add(txtNombrePeces);
        txtNombrePeces.setColumns(10);

        btnAfegir = new JButton("Afegir");
        btnAfegir.setBounds(10, 70, 320, 30);
        btnAfegir.setFont(font);
        centerPanel.add(btnAfegir);

        checkBox = new JCheckBox("Output a fitxer txt");
        checkBox.setBounds(10, 110, 200, 20);
        checkBox.setFont(font);
        centerPanel.add(checkBox);

        lblFitxer = new JLabel("Nom de fitxer:");
        lblFitxer.setBounds(10, 140, 150, 20);
        lblFitxer.setFont(font);
        centerPanel.add(lblFitxer);

        txtFitxer = new JTextField();
        txtFitxer.setBounds(130, 140, 200, 20);
        txtFitxer.setFont(font);
        centerPanel.add(txtFitxer);
        txtFitxer.setColumns(10);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Tipus de peça","Quantitat"});
        tabla = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(20, 181, 311, 137);
        centerPanel.add(scrollPane);

        btnEnviarOrdre = new JButton("Enviar Ordre");
        btnEnviarOrdre.setBounds(0, 323, 341, 30);
        btnEnviarOrdre.setFont(font);
        centerPanel.add(btnEnviarOrdre);

		btnAfegir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String comanda = cmbTipusPeces.getSelectedItem() + ";" + txtNombrePeces.getText();
				comandes.add(comanda);
				tableModel.addRow(new Object[] {cmbTipusPeces.getSelectedItem(),txtNombrePeces.getText()});
							}
		});

		btnEnviarOrdre.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lanzarSumador(comandes,outputTXT,txtFitxer.getText());
			}
		});

		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				outputTXT = checkBox.isSelected();
			}
		});
	}

	
	
	/**
     * Llanza el proces de fabricacio amb les comandes especificades.
     * @param comandes Llista de comandes a processar.
     * @param outputTxt Boolea que indica si s'ha de generar un fitxer de sortida en format txt.
     * @param nomFitxer Nom del fitxer de sortida en cas que es vulgui generar.
     */
	public static void lanzarSumador(List<String> comandes,boolean outputTxt, String nomFitxer) {

		try {

			String clase = "clases.Manufacture";
			String javaHome = System.getProperty("java.home");
			String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
			String classpath = System.getProperty("java.class.path");
			String className = clase;
			List<String> command = new ArrayList<>();

			command.add(javaBin);
			command.add("-cp");
			command.add(classpath);
			command.add(className);

			for (int i = 0; i < comandes.size(); i++) {
				command.add(comandes.get(i));
			}

			ProcessBuilder builder = new ProcessBuilder(command);

			Process process;

			if (!outputTxt) {
				process = builder.inheritIO().start();
				process.waitFor();
				System.out.println(process.exitValue());
			} else {
				File outputFile = new File("./src/fitxers/"+nomFitxer+".txt");
				process = builder.redirectOutput(outputFile).start();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * Metode principal que inicia l'aplicacio.
     * @param args Arguments de linia de comandes (no s'utilitzen).
     */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Order frame = new Order();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
