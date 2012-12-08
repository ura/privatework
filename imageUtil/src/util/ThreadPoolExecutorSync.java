package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.StaticUtil.sleep;

/**
 *
 * 投機的実行をする場合を考えたクラス。
 * 大量にキューイングした場合、無駄な処理が多くなる可能性を避ける。
 * 投機的実行の結果は、１TASKしか正当な答えを返さない。
 * それ以外はNULLを返す。
 *
 *
 *
 */
public class ThreadPoolExecutorSync {

	public ThreadPoolExecutorSync(int poolsize, int waitms) {
		super();
		threadCount = poolsize;
		this.waitms = waitms;

		ex = new ThreadPoolExecutor(threadCount, threadCount, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public ThreadPoolExecutorSync() {

		ex = new ThreadPoolExecutor(threadCount, threadCount, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	}

	private static Logger log = LoggerFactory
			.getLogger(ThreadPoolExecutorSync.class);

	private int threadCount = 10;
	private int waitms = 50;
	private ThreadPoolExecutor ex = null;

	/**
	 * キューイングして、入れるのを待つ。
	 * @param task
	 * @return
	 */
	public <V> Future<V> submit(Callable<V> task) {
		while (true) {
			//不正確だがブロックしておく。

			if (ex.getActiveCount() < threadCount) {
				log.info("TASK投入。{}", task);
				Future<V> submit = ex.submit(task);

				sleep(waitms);
				return submit;

			} else {
				log.debug("TASK投入まち。{}", task);
				sleep(waitms);

			}
		}

	}

	/**
	 * タスクの投機的実行をします。
	 * なお、実行予約をしてしまったタスクがある場合、
	 * それらのタスクが完了するまで、戻って来ません。
	 * 理由は、失敗するであろうタスクであってもリソースを保持していることによる
	 * 影響が考えられる為です。
	 *
	 * @param asList
	 * @return
	 */
	public <V> V invokeAll(List<Callable<V>> asList) {

		V result;
		try {

			List<Future<V>> list = new ArrayList<>();

			for (int i = 0; i < asList.size(); i++) {

				Callable<V> c = asList.get(i);

				Future<V> submit = submit(c);
				list.add(submit);

				V barcode = getTaskResult(list);
				if (barcode != null) {
					//while (!isRemainTask(list)) {
					//	sleep(50l);
					//}
					return barcode;
				}

			}
			while (isRemainTask(list)) {
				result = getTaskResult(list);
				if (result != null) {
					return result;
				}
				Thread.sleep(1000);
			}
			result = getTaskResult(list);
			if (result != null) {
				return result;
			}
		} catch (InterruptedException e) {
			log.info("シャットダウンの中止と思われるInterruptedExceptionが発生。{}",
					e.getMessage());

		} catch (ExecutionException e) {
			log.error("想定外のエラー", e);
		}
		log.info("TASKの投機的実行をしましたが、TASKはすべて失敗しました。");
		return null;

	}

	/**
	 * 予約してしまったタスクが残っているか？
	 * @param list
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private <V> boolean isRemainTask(List<Future<V>> list)
			throws InterruptedException, ExecutionException {

		for (Future<V> future : list) {
			if (!future.isDone()) {
				return true;
			}

		}
		return false;
	}

	/**
	 * 結果を出したタスクはあるか？
	 * @param list
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private <V> V getTaskResult(List<Future<V>> list)
			throws InterruptedException, ExecutionException {
		V result = null;
		for (Future<V> future : list) {
			if (future.isDone()) {
				result = future.get();

				if (result != null) {
					log.info("結果を戻します。:{}", result);
					return result;
				}
			}

		}
		return result;
	}
}
