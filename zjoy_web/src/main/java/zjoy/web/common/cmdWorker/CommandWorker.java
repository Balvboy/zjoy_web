package zjoy.web.common.cmdWorker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

public class CommandWorker {
	
	final long defauktPartJobTimeout = 15000;
	
	public MyResultHandler doWork(CommandLine generateCommandLine) throws IOException {
		return doWork(generateCommandLine,defauktPartJobTimeout);
	}
	
	public MyResultHandler doWork(final CommandLine commandLine, final long jobTimeout) throws IOException {

		ExecuteWatchdog watchdog = null;
		MyResultHandler resultHandler;

		final Executor executor = new DefaultExecutor();
		//设置执行路径
		//executor.setWorkingDirectory(new File("c:\\code"));
		//执行文件时0，执行命令时1
		int[] value = {0,1};
		executor.setExitValues(value);
		//executor.setStreamHandler();
		if (jobTimeout > 0) {
			watchdog = new ExecuteWatchdog(jobTimeout);
			executor.setWatchdog(watchdog);
		}
		
		resultHandler = new MyResultHandler(watchdog);
	
		executor.execute(commandLine, resultHandler);

		return resultHandler;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		MyResultHandler printResult;
		
		/*CommandLine generateCommandLine = new CommandLine("c:/code/wait10.bat");*/
		//黑色字体，outThread
		/*CommandLine generateCommandLine = new CommandLine(CommandLine.parse("ping 127.1 /n 5 >nul"));*/
		CommandLine generateCommandLine = new CommandLine(CommandLine.parse("ping 172.16.244.87"));
		/*CommandLine generateCommandLine = new CommandLine(CommandLine.parse("ipconfig"));*/
		//红色字体，errorThread
		/*String line = "e:\\test\\wkhtmltopdf\\wkhtmltopdf.exe --window-status 'myComplete' --page-size A4 --header-left '企业信用报告' --header-right \"企嘉科技    \" --header-line --footer-center '[page]/[toPage]' --footer-right \"企嘉科技    \" --header-spacing 10  --footer-line --footer-spacing 2 --margin-bottom 2cm --margin-top 3cm http://localhost:8081/entplus/email/index.html e:\\test.pdf";
		CommandLine generateCommandLine = new CommandLine(CommandLine.parse(line));*/
		
		try {
			CommandWorker cw = new CommandWorker();
			printResult = cw.doWork(generateCommandLine);
			printResult.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
