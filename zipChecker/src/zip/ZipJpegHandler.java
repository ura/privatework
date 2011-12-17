package zip;

import image.Img;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipJpegHandler implements ZipUtilHandler {
	private static Logger log = LoggerFactory.getLogger(ZipJpegHandler.class);

	private SortedSet<String> topNameSet = new TreeSet<String>();

	private Collection<Img> imgList = new ArrayList<Img>();
	private Collection<String> entryList = new ArrayList<String>();

	private LinkedList<String> current = new LinkedList<String>();

	public Collection<String> getEntryList() {
		return entryList;
	}

	private int TOP_COUNT = 10;
	private COLLECT_MODE mode;

	enum COLLECT_MODE {
		NONE, COLOR, ALL
	}

	public Collection<Img> getImgList() {
		return imgList;
	}

	public ZipJpegHandler() {
		this.mode = COLLECT_MODE.NONE;
	}

	public ZipJpegHandler(COLLECT_MODE mode) {
		this.mode = mode;
	}

	public void hanlde(ZipUtil util, ZipFile zip, ZipEntry ze) {
		String name = ze.getName();
		if (name.endsWith(".jpg")) {

			File createFile = util.createFile(zip, ze, "jpg");
			log.debug(createFile.getAbsolutePath());
			Img img = new Img(createFile);

			switch (this.mode) {
			case ALL:
				imgList.add(img);
				entryList.add(nowEntry(ze));
				break;
			case COLOR:
				if (img.isColor()) {

					imgList.add(img);
					entryList.add(nowEntry(ze));
				}
				img.freeMemory();
				break;
			case NONE:

				break;

			default:
				break;
			}
			img.freeMemory();

		}

	}

	/**
	 * あまり参考にならない・・・・
	 *
	 * @param zip
	 * @param ze
	 * @return
	 */
	private boolean size(ZipFile zip, ZipEntry ze) {
		if (ze.getSize() > 100 * 1000) {
			return true;
		} else {
			return false;
		}
	}

	public boolean needCheck(ZipUtil util, ZipFile zip, ZipEntry ze) {
		boolean result = true;
		String name = ze.getName();
		if (name.endsWith(".jpg")) {
			result = topNameSet.contains(name);
		}

		return result;
	}

	@Override
	public void end(ZipUtil util, ZipFile zip) throws ZipCheckException {

	}

	private String nowEntry(ZipEntry ze) {
		StringBuilder sb = new StringBuilder();
		for (String zipName : current) {
			sb.append(zipName).append(":");
		}
		sb.append(ze.getName());

		return sb.toString();
	}

	@Override
	public void down(ZipFile zip, ZipEntry ze) {
		current.addLast(ze.getName());
	}

	@Override
	public void up(ZipFile zip, ZipEntry ze) {
		current.removeLast();
	}

	@Override
	public void start(ZipUtil util, ZipFile zip) throws ZipCheckException {
		Enumeration<ZipEntry> e = zip.getEntries();

		HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();

		while (e.hasMoreElements()) {
			ZipEntry ze = e.nextElement();
			entryMap(map, ze);
		}


		for (Set<String> nameSet : map.values()) {
			int count = 0;
			for (String s : nameSet) {

				if (count < TOP_COUNT) {
					this.topNameSet.add(s);
				}else{
					break;
				}

				count++;
			}
		}

		if (log.isInfoEnabled()) {
			for (String s : topNameSet) {
				log.info("zip top filename:{}", s);
			}
		}

	}

	private void entryMap(HashMap<String, Set<String>> map, ZipEntry ze) {
		Set<String> nameSet;
		String name = ze.getName();


		String dir;
		 String[] args = name.split("/");
		if (args.length == 1) {
			dir = "root";

		} else {
			dir = args[args.length-2];

		}

		Set<String> set = map.get(dir);
		if (set == null) {
			set = new TreeSet<String>();
			map.put(dir, set);
		}

		set.add(ze.getName());
	}

}
