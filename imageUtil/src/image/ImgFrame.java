package image;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.StaticUtil;

public class ImgFrame extends AbstractAction implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ImgFrame.class);

	private JButton addButton;
	private JFrame frame;
	private boolean nowView = true;

	private JMenu mn1;

	public void viewStop(Collection<Img> list) {

		if (list.size() == 0) {

			log.warn("LIST SIZE 0.NOT VIEW.");
			return;
		}

		frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);

		createMenu();

		Container contentPane = frame.getContentPane();

		JPanel imgPanel = createImgPanel(list);

		contentPane.add(imgPanel, BorderLayout.CENTER);
		// contentPane.add(buttonPane, BorderLayout.SOUTH);

		frame.setSize(new Dimension(600, 800));
		frame.pack();
		frame.setVisible(true);

		waitClose();
		this.putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
	}

	public void viewStop(Collection<Img> list, Collection<Img> list2) {

		if (list.size() == 0) {

			log.warn("LIST SIZE 0.NOT VIEW.");
			return;
		}

		frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);

		createMenu();

		Container contentPane = frame.getContentPane();

		JPanel imgPanel = createImgPanel(list);
		JPanel imgPanel2 = createImgPanel(list2);
		contentPane.add(imgPanel, BorderLayout.CENTER);
		contentPane.add(imgPanel2, BorderLayout.SOUTH);

		frame.setSize(new Dimension(600, 800));
		frame.pack();
		frame.setVisible(true);

		waitClose();
		this.putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
	}

	private JPanel createImgPanel(Collection<Img> list) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(list.size() < 6 ? 1 : 2,
				list.size() < 6 ? list.size() : 5));

		for (Img img : list) {

			img.loadImage();

			BufferedImage image = img.loadImage(300, 300);
			ImageIcon icon = new ImageIcon(image);
			JLabel label = new JLabel(icon);
			label.setToolTipText(img.getInfo());
			panel.add(label);

		}
		return panel;
	}

	private void createMenu() {
		JMenuBar mb = new JMenuBar();
		JMenu mn1 = new JMenu("操作");

		// ニーモック割り当て
		mn1.setMnemonic(KeyEvent.VK_B);

		JMenuItem m1 = new JMenuItem("閉じる");

		frame.setJMenuBar(mb);
		mb.add(mn1);
		mn1.add(m1);

		// ニーモック割り当て
		m1.addActionListener(this);
		m1.setMnemonic(KeyEvent.VK_B);

	}

	private void createButton() {
		JPanel buttonPane = new JPanel();
		addButton = new JButton("追加");
		// 「追加」ボタンとアクション・リスナーの関連付け
		addButton.addActionListener(this);
		buttonPane = new JPanel();
		buttonPane.add(addButton);
	}

	public void waitClose() {

		while (this.nowView) {
			StaticUtil.sleep(500l);

		}

	}

	@Override
	public void actionPerformed(ActionEvent event) {

		frame.dispose();
		this.nowView = false;
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

		this.nowView = false;

	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.nowView = false;

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

}
