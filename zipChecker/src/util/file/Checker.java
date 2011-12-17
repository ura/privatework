package util.file;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * すでにファイルをチェックしているか調べるためのユーティル
 * @author poti
 *
 */
public class Checker {


	private File root;
	private File datatxt;
	private Set<Check> checkSet;

	private static final String DATA_FILE_NAME="zipcheck.ser";


	public Checker(File rootPath){

		//TODO ファイルの存在をチェック
		checkSet=new HashSet<Check>();

		datatxt=new File(rootPath.getPath()+"\\"+DATA_FILE_NAME);
		if(datatxt.exists()){
			checkSet=(Set<Check>)ObjectUtil.load(datatxt.getPath());
		}else{
			checkSet=new HashSet<Check>();
		}


	}

	public  void  registration(File f){

		checkSet.add(new Check(f));

	}

	public  boolean  check(File f){

		Check check = new Check(f);
		return checkSet.contains(check);



	}

	public  void  save(){

		Iterator<Check> iterator = checkSet.iterator();
		while (iterator.hasNext()) {
			Check check = (Check) iterator.next();
			if(check.isOld()){
				iterator.remove();
			}

		}



		ObjectUtil.save(datatxt.getPath(),checkSet);

	}



}
