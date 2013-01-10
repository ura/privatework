package book;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ClipBoard;
import book.BookInfoRepo.State;
import book.rpc.BookServer;
import book.webapi.BookInfo;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import static util.StaticUtil.sleep;

/**
 * 新刊の探査をつける。
 * ステータスをつける。
 * @author name
 *
 */
public class BookInfoRepoGUI extends JFrame {

	private static Logger log = LoggerFactory.getLogger(BookInfoRepoGUI.class);

	private static final int MAX = 150;

	private BookInfoRepo repo;
	private List<BookInfo> list = new ArrayList<>();
	private boolean init;
	private String key;
	private final JTable tb = new JTable(MAX, 2);
	private DefaultTableColumnModel columnModel;

	public BookInfoRepoGUI() {

		repo = new BookInfoRepo();

		repo.load();

		BookServer server = new BookServer(repo);
		server.startThread();

		getContentPane().setLayout(null);

		final JTextField tf = new JTextField("Hello World!!", 15);

		tf.setBounds(20, 20, 640, 30);
		getContentPane().add(tf);

		columnModel = (DefaultTableColumnModel) tb.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(30);

		JComboBox<BookInfoRepo.State> comboBox = new JComboBox<>();
		comboBox.addItem(BookInfoRepo.State.HAVE);
		comboBox.addItem(BookInfoRepo.State.BAT);
		comboBox.addItem(BookInfoRepo.State.WANT);
		DefaultCellEditor cellEditor = new DefaultCellEditor(comboBox);
		columnModel.getColumn(0).setCellEditor(cellEditor);

		columnModel.getColumn(1).setPreferredWidth(550);

		tb.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {

				if (!init) {

					int f = e.getFirstRow();
					int l = e.getLastRow();

					log.warn("変更行:{}:{}", f, l);
					BookInfo bookInfo = list.get(f);
					State valueAt = (State) tb.getValueAt(f, 0);

					repo.update(bookInfo, valueAt);
				}

			}
		});

		JScrollPane sp = new JScrollPane(tb);
		sp.setPreferredSize(new Dimension(230, 80));
		sp.setBounds(20, 80, 640, 650);
		getContentPane().add(sp);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("JTextFieldTest");
		setSize(700, 800);
		setVisible(true);
		setAlwaysOnTop(true);

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						sleep(10);

						set(tb, tf.getText());
					} catch (Exception e) {

						log.error("検索スレッドで想定外のエラー", e);
						sleep(10000);
					}
				}

			}
		}).start();

		while (true) {
			String str = ClipBoard.getClipboard();

			tf.setText(str);

		}
	}

	private void clear(JTable tb) {

		list.clear();
		for (int i = 0; i < MAX; i++) {

			tb.setValueAt(null, i, 0);
			tb.setValueAt("", i, 1);
		}

	}

	/**
	 *
	 */
	private Set<BookInfo> createBookSummary(Set<BookInfo> set) {

		SortedSetMultimap<String, BookInfo> map = TreeMultimap.create();

		Set<BookInfo> result = new TreeSet<>();

		result.addAll(set);

		return result;

	}

	private void set(JTable tb, String keyword)

	{

		if (!keyword.equals(key)) {

			init = true;

			clear(tb);
			key = keyword;
			System.out.println("QUERY:" + keyword);

			try {
				int i = 0;
				{
					Set<BookInfo> set = repo.get(State.HAVE,
							keyword.split("[ 　\t\\[\\]第]"));

					for (BookInfo bookInfo : createBookSummary(set)) {
						if (i >= MAX) {
							return;
						}

						tb.setValueAt(State.HAVE, i, 0);
						tb.setValueAt(bookInfo.getInfo(), i++, 1);
						list.add(bookInfo);
					}
				}
				{
					Set<BookInfo> set = repo.get(State.BAT,
							keyword.split(" 　\t\\[\\]第"));
					for (BookInfo bookInfo : createBookSummary(set)) {
						if (i >= MAX) {
							return;
						}
						tb.setValueAt(State.BAT, i, 0);
						tb.setValueAt(bookInfo.getInfo(), i++, 1);
						list.add(bookInfo);
					}
				}
				{
					Set<BookInfo> set = repo.get(State.WANT,
							keyword.split(" 　\t\\[\\]第"));
					for (BookInfo bookInfo : createBookSummary(set)) {
						if (i >= MAX) {
							return;
						}
						tb.setValueAt(State.WANT, i, 0);
						tb.setValueAt(bookInfo.getInfo(), i++, 1);
						list.add(bookInfo);
					}
				}
			} finally {
				init = false;
			}

		}

	}

	public static void main(String[] args) {

		new BookInfoRepoGUI();

	}

}
