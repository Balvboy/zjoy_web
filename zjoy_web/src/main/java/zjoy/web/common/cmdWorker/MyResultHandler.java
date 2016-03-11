package zjoy.web.common.cmdWorker;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

public class MyResultHandler extends DefaultExecuteResultHandler {

	private ExecuteWatchdog watchdog;

	public MyResultHandler(final ExecuteWatchdog watchdog) {
		this.watchdog = watchdog;
	}

	@Override
	public void onProcessComplete(final int exitValue) {
		super.onProcessComplete(exitValue);
		System.out.println("命令执行完成");
	}

	@Override
	public void onProcessFailed(final ExecuteException e) {
		super.onProcessFailed(e);
		if (watchdog != null && watchdog.killedProcess()) {
			System.out.println("命令执行超时");
		} else {
			System.err.println("命令执行失败 : " + e.getMessage());
		}
	}
}