package image;

import image.Histogram.CloorBlock;
import image.compare.CompositComparator;
import image.compare.ImgComparator;
import image.compare.SimpleHistogramComparator;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileWalker;
import util.file.ObjectUtil;
import dir.DirCollector;

public class ImageDataTree implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5665934032487730649L;

	private static int MAX_DIPTH = 5;
	private static Logger log = LoggerFactory.getLogger(ImageDataTree.class);

	private int cloorBacuket = 5;

	private Map<String, ImageDataTree> map = new HashMap<String, ImageDataTree>();
	private ImageDataTree parentTree;
	private int depth;
	private String key;
	private File path;
	private List<Img> list = new ArrayList<Img>();

	public ImageDataTree(int depth, String key, File path) {
		super();
		this.depth = depth;
		this.key = key;
		this.setPath(path);
	}

	public ImageDataTree(int depth, String key, String path) {
		this(depth, key, new File(path));

	}

	public void walk() {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(getPath(), srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFileFullPath();

		int count = 0;

		for (String path : allFileFullPath) {
			if (path.endsWith(".jpg") || path.endsWith(".jpeg")
					|| path.endsWith(".tif") || path.endsWith(".tif")) {

				Img img = new Img(path);
				img.createHistogram(true);
				add(img);
				count++;

				if (count % 10 == 0) {

					log.info("�������ł��E�E�E�E  �I������: " + count);
				}
			}

		}

	}

	private String getSaveFileName() {
		return (this.getPath().getName() + "_" + this.key + ".sar")
				.replace(':', '_');
	}

	public void save() {
		log.info("sava {}", getSaveFileName());
		ObjectUtil.save(getSaveFileName(), this);
	}

	public ImageDataTree load() {

		ImageDataTree tree = (ImageDataTree) ObjectUtil.load(getSaveFileName());
		return tree;
	}

	public List<Img> getAllImage() {

		List<Img> resultList = new ArrayList<Img>();
		resultList.addAll(list);

		for (ImageDataTree tree : map.values()) {
			List<Img> allImage = tree.getAllImage();
			resultList.addAll(allImage);
		}

		return resultList;

	}

	public void add(Img img) {

		if (map.size() == 0) {
			list.add(img);
		} else {
			addChild(img);
		}

		if (list.size() >= 10 && this.depth < MAX_DIPTH) {
			for (Img i : list) {
				addChild(i);

			}
			list.clear();
		}

	}

	private void addChild(Img img) {
		CloorBlock cb = img.getHistogram().getHistogramBlockArray()[0][0]
				.getCloorBlock(depth);
		String tmpkey = createKey(cb);
		ImageDataTree tree = map.get(tmpkey);
		if (tree == null) {
			tree = new ImageDataTree(this.depth + 1, tmpkey, this.getPath());
			tree.setParentTree(this);
			map.put(tmpkey, tree);
		}

		tree.add(img);

	}

	private String createKey(CloorBlock cb) {
		String tmpKey;

		if (cb != null) {

			// ��depth�ʂ̐F�O���t��
			String key1 = cb.getRed() + ":" + cb.getGreen() + ":"
					+ cb.getBlue();
			// ���̊����ŃO���[�s���O
			String key2 = String.valueOf(cb.rate()
					/ (Histogram.CLOOR_RATE / cloorBacuket));
			tmpKey = key1 + ":" + key2;
		} else {
			tmpKey = "null";
		}
		return tmpKey;
	}

	private void getKey(StringBuilder sb) {

		if (this.parentTree != null) {
			this.parentTree.getKey(sb);
		}
		sb.append(key + "\t");

	}

	public String getKey() {

		StringBuilder stringBuffer = new StringBuilder();
		getKey(stringBuffer);
		return stringBuffer.toString();

	}

	public void log() {

		log.info(getKey() + " : size=" + list.size());
		for (Img i : list) {
			log.info(i.toString());
		}

		for (Map.Entry<String, ImageDataTree> e : map.entrySet()) {
			String k = e.getKey();
			ImageDataTree t = e.getValue();

			t.log();

		}

	}

	// TODO ��v�x�̓��W�b�N�̈�Ȃ̂ŁA�R���p���[�^�Ɋ܂߂�ׂ����̂̂͂��A�A�A
	public Collection<Img> check(Img img, int def) {
		return check(img, def, new SimpleHistogramComparator());
	}

	/**
	 *
	 *
	 * @param img
	 * @param def
	 *            ��v�x���w�肷��B0�͊��S��v�B10000�܂ŁB
	 * @return
	 */
	public Collection<Img> check(Img img, int def, ImgComparator comparator) {

		CompositComparator compositComparator = new CompositComparator(
				comparator, def);
		check(img, compositComparator);
		compositComparator.printInfo(img);

		return compositComparator.getSameList();

	}

	public CompositComparator check(Img img, CompositComparator comparator) {

		if (map.size() == 0) {
			for (Img listImg : list) {
				int result = comparator.comparate(img, listImg);

			}
		} else {
			checkChild(img, comparator);

		}

		return comparator;

	}

	private void checkChild(Img img, CompositComparator comparator) {

		CloorBlock cb = img.getHistogram().getHistogramBlockArray()[0][0]
				.getCloorBlock(depth);
		String createKey = createKey(cb);
		ImageDataTree child = map.get(createKey);

		if (child == null) {
			// TODO
			log.info("��v����摜�͂Ȃ��l�ł����A�A�A�A:{} :{}", getKey(), img.getImgFile()
					.getName());

		} else {
			child.check(img, comparator);
		}

	}

	public Map<String, ImageDataTree> getMap() {
		return map;
	}

	public ImageDataTree getParentTree() {
		return parentTree;
	}

	public void setParentTree(ImageDataTree parentTree) {
		this.parentTree = parentTree;
	}

	public List<Img> getList() {
		return list;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public File getPath() {
		return path;
	}

}
