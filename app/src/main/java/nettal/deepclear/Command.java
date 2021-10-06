package nettal.deepclear;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

class Command
{
	Process process;
	BufferedReader dis; //命令输出(stdout)
//	BufferedReader Edis;  //命令输出(stderr)
	DataOutputStream dos;

	Command() throws IOException{
		process = Runtime.getRuntime().exec("su");
		dis = new BufferedReader(new InputStreamReader(process.getInputStream()));
		//	Edis = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		dos = new DataOutputStream(process.getOutputStream());
	}
	String exec(String cmd) throws IOException{
		StringBuilder sb = new StringBuilder();
		String stdout = null;
		//	String stderr = null;
		dos.writeBytes(cmd);
		dos.writeByte('\n');  //"回车"
		dos.flush();
		dos.writeBytes("echo _E_O_F_\n");  //结束标记符
		dos.flush();
		//	dos.writeBytes("exit\n");
		//	dos.flush();

		/*while(((stdout = dis.readLine()) != null)
		 || ((stderr = Edis.readLine()) != null)){
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
		while(true){
			stdout = dis.readLine();
			//	System.out.println(dis.readLine());
			//	sb.append(stdout);
			if(stdout.equals("_E_O_F_"))
				break;
			sb.append(stdout);
			sb.append('\n');
			//	System.out.println(Edis.readLine());
		}
		return sb.toString();
	}
}

