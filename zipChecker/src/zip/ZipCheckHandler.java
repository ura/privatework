package zip;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipCheckHandler implements ZipUtilHandler {

	private int nozipcount = 0;
	private int zipcount = 0;

	public void hanlde(ZipUtil util, ZipFile zip, ZipEntry ze) {
		util.testRead(zip, ze);
	}

	public boolean needCheck(ZipUtil util, ZipFile zip, ZipEntry ze) {

		boolean result = true;
		// ����
		if (ze.getName().endsWith(".zip")) {

			//ZIP�̒���ZIP���񓚂������Ƃ�����΁AOK��
			if (zipcount > 3) {
				result = false;
			}

		} else {
			//10�����x�񓚂ł��Ă���΁AOK��
			if (zipcount > 10) {
				result = false;
			}
		}

		// �W�v
		if (ze.getName().endsWith(".zip")) {
			zipcount++;
		} else {
			nozipcount++;
		}

		return true;
	}

	@Override
	public void end(ZipUtil util, ZipFile zip) throws ZipCheckException {
		if (zipcount < 2 && nozipcount < 7) {
			throw new ZipCheckException("�s���ȃt�@�C���̋C�����܂�", State.FEW_FILE);
		}

	}

	@Override
	public void down(ZipFile zip, ZipEntry ze) {

	}

	@Override
	public void up(ZipFile zip, ZipEntry ze) {

	}

	@Override
	public void start(ZipUtil util, ZipFile zip) throws ZipCheckException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}
}
