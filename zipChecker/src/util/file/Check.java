package util.file;

import java.io.File;
import java.io.Serializable;

public class Check implements Serializable {

	public static long OLD_TIME = 1000l * 60 * 60 * 24 * 3;

	private String fileName;
	private long size;
	private long lastCheck;

	public Check() {

	}

	public Check(File f) {

		this.fileName = f.getName();
		this.size = f.length();
		this.lastCheck = System.currentTimeMillis();

	}

	public Check(String line) {

		String[] args = line.split("\t");

		this.fileName = args[0];
		this.size = Long.parseLong(args[1]);
		this.lastCheck = Long.parseLong(args[2]);

	}

	public boolean isOld() {
		long now = System.currentTimeMillis();

		if (now - this.lastCheck > OLD_TIME) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Check other = (Check) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLastCheck() {
		return lastCheck;
	}

	public void setLastCheck(long lastCheck) {
		this.lastCheck = lastCheck;
	}

}
