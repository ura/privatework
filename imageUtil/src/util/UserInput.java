package util;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 *
 * 標準入力対応用簡易クラス。 マルチスレッドのことは考慮せず。
 *

 *
 */
public class UserInput {

	private static BufferedReader br;
	static {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	private static String st;
	private static List<String> list = new ArrayList<String>();

	public static boolean isInput() {
		try {

			// 入力された文字を変数に代入
			st = br.readLine();
			if (!st.equals("")) {
				return true;

			} else {
				return false;

			}

		} catch (IOException e1) {
			throw new IllegalStateException(e1);
		}
	}

	public static String getUserInput() {

		return st;

	}

	public static <V> V selectOne(Collection<V> list) {

		System.out.println("候補を表示します。一つ選択してください");
		Map<String, V> m = new HashMap<String, V>();

		int i = 0;
		for (V s : list) {

			System.out.println(i + "\t" + toString(s));
			m.put(String.valueOf(i), s);
			i++;
		}

		if (UserInput.isInput()) {

			String userInput = UserInput.getUserInput();
			V v = m.get(userInput);
			return v;

		} else {
			return null;

		}

	}

	public static <V> List<V> selectMany(Collection<V> list) {

		System.out.println("候補を表示します。複数選択できます。");
		Map<String, V> m = new HashMap<String, V>();

		List<V> r = new ArrayList<V>();

		int i = 0;
		for (V s : list) {

			System.out.println(i + "\t" + toString(s));
			m.put(String.valueOf(i), s);
			i++;
		}

		if (UserInput.isInputs()) {
			System.out.println("下記の対象が選択されました。");
			for (String k : UserInput.getUserInputs()) {
				V remove = m.remove(k);

				if (remove != null) {
					System.out.println(k + "\t" + toString(remove));
					r.add(remove);
				} else {
					System.out.println("正しい除外対象が指定されませんでした。");
					return null;
				}

			}

		} else {
			System.out.println("対象が指定されませんでした。");
		}

		return r;

	}

	public static <V> Collection<V> selectManySwing(List<V> list) {

		String[] names = { "Boolean", "String" };

		Object[][] records = new Object[list.size()][2];
		for (int i = 0; i < records.length; i++) {
			records[i] = new Object[] { Boolean.TRUE, toString(list.get(i)) };
		}

		TableModel model = new DefaultTableModel(records, names) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return true;
			}
		};

		JTable table = new JTable(model);

		JComboBox<TableModel> comboBox = new JComboBox<TableModel>();

		TableCellEditor editor = new DefaultCellEditor(comboBox);
		table.getColumnModel().getColumn(1).setCellEditor(editor);

		JButton addButton = new JButton("決定");

		addButton.setMaximumSize(new Dimension(Short.MAX_VALUE, addButton
				.getPreferredSize().height));

		class MyActionListener implements ActionListener {

			private boolean f = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				this.f = true;
			}

			public void inputwait() {

				while (true) {
					if (!this.f) {

						StaticUtil.sleep(100l);

					} else {
						break;
					}
				}

			}
		}
		;
		MyActionListener listener = new MyActionListener();

		addButton.addActionListener(listener);

		JFrame frame = new JFrame("Joey Table");
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, 2));
		frame.getContentPane().add(p);
		p.add(new JScrollPane(table));
		p.add(addButton);

		frame.setSize(1000, 640);
		frame.setVisible(true);

		listener.inputwait();

		frame.setVisible(false);
		frame.dispose();

		List<V> result = new ArrayList<V>();
		for (int i = 0; i < records.length; i++) {

			if ((Boolean) model.getValueAt(i, 0)) {
				result.add(list.get(i));
			}

		}

		return result;

	}

	public static List<String> getUserInputsSwing(String title, int size) {

		JButton addButton = new JButton("決定");

		class MyActionListener implements ActionListener {

			private boolean f = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				this.f = true;
			}

			public void inputwait() {

				while (true) {
					if (!this.f) {
						StaticUtil.sleep(100l);
					} else {
						break;
					}
				}

			}
		}
		;
		MyActionListener listener = new MyActionListener();

		addButton.addActionListener(listener);

		JFrame frame = new JFrame(title);
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(size + 1, 1));
		frame.getContentPane().add(p);

		List<JTextField> l = new ArrayList<JTextField>();
		for (int i = 0; i < size; i++) {
			l.add(new JTextField());
			p.add(l.get(i));
		}
		p.add(addButton);

		frame.setSize(640, size * 30 + 100);
		frame.setVisible(true);

		listener.inputwait();
		frame.setVisible(false);
		frame.dispose();

		List<String> aList = new ArrayList<String>();
		for (JTextField jTextField : l) {
			String s = jTextField.getText();
			if (s != null && s.trim().length() != 0) {
				aList.add(s.trim());
			}
		}

		return aList;

	}

	private static <V> String toString(V v) {

		if (v instanceof File) {
			return ((File) v).getAbsolutePath() + "　　　　　"
					+ (((File) v).length() / 1024 / 1024) + "M";
		} else {
			return v.toString();
		}

	}

	public static boolean isInputs() {
		list.clear();
		try {

			while (true) {
				// 入力された文字を変数に代入
				String st = br.readLine();

				if (!st.equals("")) {
					list.add(st);
				} else {
					break;
				}
			}

		} catch (IOException e1) {
			throw new IllegalStateException(e1);
		}

		if (list.size() != 0) {
			return true;
		} else {
			return false;
		}

	}

	public static List<String> getUserInputs() {

		return list;

	}

	/**
	 * このクラスを介した入力の自動化をする。
	 * 主にテスト用
	 * @param retry
	 * @param str
	 */
	public static void autoInput(int retry, final String... str) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < retry; i++) {
			for (String string : str) {
				sb.append(string).append("\n");
			}

		}

		br = new BufferedReader(new StringReader(sb.toString()));

	}
}
