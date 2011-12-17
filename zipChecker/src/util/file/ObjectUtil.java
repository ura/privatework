package util.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectUtil {

	private static Logger log = LoggerFactory.getLogger(ObjectUtil.class);

	@SuppressWarnings("unchecked")
	public static Object load(String path)  {

		try {
			FileInputStream fos = new FileInputStream(path);
			ObjectInputStream oos = new ObjectInputStream(fos);

			Object o = oos.readObject();

			oos.close();
			fos.close();

			return o;
		} catch (FileNotFoundException e) {
			log.error("LOAD ERROR",e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			log.error("LOAD ERROR",e);
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			log.error("LOAD ERROR",e);
			throw new IllegalArgumentException(e);
		}

	}

	public static void save(String path, Object o)  {

		try {
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);

			oos.flush();
			oos.close();
			fos.close();
		} catch (Exception e) {
			log.error("SAVE ERROR",e);

			throw new IllegalArgumentException(e);
		}


	}

	public ObjectUtil() {
		super();
	}

}