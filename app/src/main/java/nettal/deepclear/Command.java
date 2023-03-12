package nettal.deepclear;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

final class Command {
    Process process;
    BufferedReader bufferedInputReader; //命令输出(stdout)
    //	BufferedReader bufferedErrorReader;  //命令输出(stderr)
    DataOutputStream outputStream;

    public static Command getCommand() throws IOException {
        return new Command().initial();
    }

    public static boolean checkSU() {
        try {
            Command command = getCommand();
            command.exec("am");
            command.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Command() {
    }

    public Command initial() throws IOException {
        try {
            process = Runtime.getRuntime().exec("su");
            bufferedInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //	bufferedErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            outputStream = new DataOutputStream(process.getOutputStream());
            return this;
        } catch (IOException e) {
            close();
            throw new IOException(e);
        }
    }

    String exec(String cmd) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String stdout;
            //	String stderr = null;
            outputStream.writeBytes(cmd);
            outputStream.writeByte('\n');  //"回车"
            outputStream.flush();
            outputStream.writeBytes("echo _E_O_F_\n");  //结束标记符
            outputStream.flush();
            //	outputStream.writeBytes("exit\n");
            //	outputStream.flush();

		/*while(((stdout = bufferedInputReader.readLine()) != null)
		 || ((stderr = bufferedErrorReader.readLine()) != null)){
		 if(stdout != null){
		 //	sb.append(stdout);
		 //	sb.append('\n');
		 System.out.print(stdout);
		 }
		 if(stderr != null){
		 //	sb.append(stderr);
		 //	sb.append('\n');
		 System.out.print(stderr);
		 }
		 }*/
            while (true) {
                stdout = bufferedInputReader.readLine();
                //	System.out.println(bufferedInputReader.readLine());
                //	sb.append(stdout);
                if (stdout.equals("_E_O_F_"))
                    break;
                sb.append(stdout);
                sb.append('\n');
                //	System.out.println(bufferedErrorReader.readLine());
            }
            return sb.toString();
        } catch (IOException e) {
            close();
            throw new IOException(e);
        }
    }

    public void close() {
        if (process != null)
            try {
                process.destroy();
            } catch (Exception ignored) {
            }
        if (bufferedInputReader != null)
            try {
                bufferedInputReader.close();
            } catch (Exception ignored) {
            }
        if (outputStream != null)
            try {
                outputStream.close();
            } catch (Exception ignored) {
            }
    }
}

