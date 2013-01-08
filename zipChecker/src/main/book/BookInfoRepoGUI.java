package book;

import java.awt.Dimension;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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

	public BookInfoRepoGUI() {

		repo = new BookInfoRepo();

		repo.load();

		BookServer server = new BookServer(repo);
		server.startThread();

		getContentPane().setLayout(null);

		final JTextField tf = new JTextField("Hello World!!", 15);

		tf.setBounds(20, 20, 640, 30);
		getContentPane().add(tf);

		final JTable tb = new JTable(MAX, 2);

		DefaultTableColumnModel columnModel = (DefaultTableColumnModel) tb
				.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(30);
		columnModel.getColumn(1).setPreferredWidth(550);

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

	private String key;

	private void clear(JTable tb) {
		DefaultTableColumnModel defaultTableColumnModel = new DefaultTableColumnModel();
		//tb.setColumnModel(defaultTableColumnModel);

		for (int i = 0; i < MAX; i++) {

			tb.setValueAt("", i, 0);
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
			clear(tb);
			key = keyword;
			System.out.println("QUERY:" + keyword);
			int i = 0;
			{
				Set<BookInfo> set = repo.get(State.HAVE,
						keyword.split("[ 　\t\\[\\]第]"));

				for (BookInfo bookInfo : createBookSummary(set)) {
					if (i >= MAX) {
						return;
					}

					tb.setValueAt("所持", i, 0);
					tb.setValueAt(bookInfo.getInfo(), i++, 1);
				}
			}
			{
				Set<BookInfo> set = repo.get(State.BAT,
						keyword.split(" 　\t\\[\\]第"));
				for (BookInfo bookInfo : createBookSummary(set)) {
					if (i >= MAX) {
						return;
					}
					tb.setValueAt("候補", i, 0);
					tb.setValueAt(bookInfo.getInfo(), i++, 1);
				}
			}
			{
				Set<BookInfo> set = repo.get(State.WANT,
						keyword.split(" 　\t\\[\\]第"));
				for (BookInfo bookInfo : createBookSummary(set)) {
					if (i >= MAX) {
						return;
					}
					tb.setValueAt("欲しい", i, 0);
					tb.setValueAt(bookInfo.getInfo(), i++, 1);
				}
			}
		}

	}

	public static void main(String[] args) {

		new BookInfoRepoGUI();

	}

}
