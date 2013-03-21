package me.lion328.servermauncher;

import java.awt.EventQueue;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Toolkit;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JProgressBar;

public class LauncherFrame {

	private JFrame frame;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPanel panel_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LauncherFrame window = new LauncherFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public LauncherFrame() throws Exception {
		Util.readConfig();
		
		if(MD5FileUtil.getMD5Checksum(Util.findPathJar(this.getClass()).replace("%20", " ")).contains(HTTPRequestPoster.sendGetRequest(LauncherSetting.hashURL.replace("{0}", LauncherSetting.launcherFileName + Util.getSystemJavaExet()), ""))){
			JOptionPane.showMessageDialog(null, "ตัว Launcher ไม่ใช่รุ่นล่าสุด ระบบจะพาดาวน์โหลดอัตโนมัติ...", "มีการอัพเดท", JOptionPane.INFORMATION_MESSAGE);
			Util.runURL(new URI(LauncherSetting.dlURL + "/" + LauncherSetting.launcherFileName + Util.getSystemJavaExet()));
			System.exit(0);
		}
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		
		frame = new JFrame();
		frame.setIconImage(ImageIO.read(LauncherFrame.class
				.getResourceAsStream("favicon.png")));
		frame.setResizable(false);
		frame.setBounds(100, 100, 906, 537);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int left = (d.width - frame.getWidth()) / 2;
		int top = (d.height - frame.getHeight()) / 2;
		frame.setLocation(left, top);
		frame.setTitle(LauncherSetting.title);

		textField = new JTextField();
		textField.setBounds(646, 117, 217, 23);
		textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textField.setColumns(10);
		frame.getContentPane().setLayout(null);

		passwordField = new JPasswordField();
		passwordField.setBounds(646, 170, 217, 23);
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		frame.getContentPane().add(passwordField);
		frame.getContentPane().add(textField);
		if (LauncherSetting.authURL.equals("")) {
			passwordField.setEnabled(false);
		}

		JTextPane txtpnNews = new JTextPane();
		txtpnNews.setEditable(false);
		txtpnNews.setText(HTTPRequestPoster.sendGetRequest(
				LauncherSetting.txtNewsURL, "").replace("\\n", "\n"));
		txtpnNews.setBounds(33, 85, 563, 281);
		frame.getContentPane().add(txtpnNews);

		JPanel panel = new JPanel();
		panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel.setOpaque(false);
		panel.setBounds(775, 203, 82, 29);
		frame.getContentPane().add(panel);

		panel_1 = new JPanel();
		panel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Util.runURL(new URI(LauncherSetting.registerURL));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		panel_1.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel_1.setOpaque(false);
		panel_1.setBounds(674, 209, 82, 23);
		frame.getContentPane().add(panel_1);

		final JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(33, 438, 563, 27);
		frame.getContentPane().add(progressBar);

		final JLabel lblNewLabel = new JLabel(
				"\u0E44\u0E21\u0E48\u0E21\u0E35\u0E01\u0E32\u0E23\u0E17\u0E33\u0E07\u0E32\u0E19");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(104, 410, 492, 23);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblTemplete = new JLabel();
		lblTemplete.setIcon(new ImageIcon(getClass().getResource("/me/lion328/servermauncher/bg.png")));
		lblTemplete.setBounds(0, 0, 900, 508);
		this.frame.getContentPane().add(lblTemplete);

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(textField.getText().equals("") || (new String(passwordField.getPassword()).equals("") && !LauncherSetting.authURL.equals(""))){ 
					JOptionPane.showMessageDialog(null, "ชื่อผู้ใช้หรือรหัสผ่านต้องไม่ว่างเปล่า", "ผิดพลาด", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(Util.getCustomAuth(textField.getText(), new String(passwordField.getPassword()))){
					try {
						Util.downloadPatch(lblNewLabel, progressBar, new String[]{textField.getText()});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
