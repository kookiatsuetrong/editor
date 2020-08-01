package web;
import java.io.*;
import java.util.*;
import org.springframework.ui.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
class Web {
	@GetMapping("/")
	String showHome() {
		return "index";
	}
	String slot = "abcdefghijklmnopqrstuvwxyz";
	char[] all  = slot.toCharArray();
	String random() {
		String s = "";
		for (int i = 0; i < 10; i++) {
			int r = (int)(Math.random() * 26);
			s += all[r];
		}
		return s;
	}
	
	@PostMapping("/run-c") @ResponseBody
	Result runC(String code) {
		String name    = "code-" + random();
		String compile = "cc -o " + name + " " + name + ".c";
		String execute = "./" + name;
		Result r = new Result();
		try {
			FileWriter writer = new FileWriter(name + ".c");
			writer.write(code);
			writer.close();
			Process p = Runtime.getRuntime().exec(compile);
			r.result = "";
			int k;
			InputStream pis = p.getErrorStream();
			do {
				k = pis.read();
				r.result += k >= 0 ? (char)k : "";
			} while (k != -1);
			
			Process q = Runtime.getRuntime().exec(execute);
			Killer killer = new Killer(q);
			killer.start();
			InputStream qis = q.getInputStream();
			do {
				k = qis.read();
				r.result += k >= 0 ? (char)k : "";
			} while (k != -1);
		} catch (Exception e) {
			r.result = "Error";	
		} finally {
			File f = new File(name);
			f.delete();
			File g = new File(name + ".c");
			g.delete();
		}
		return r;
	}
	
	@PostMapping("/run-java") @ResponseBody
	Result runJava(String code) {
		String name = "code-" + random() + ".java";
		String command = "java " + name;
		
		Result r = new Result();
		try {
			FileWriter writer = new FileWriter(name);
			writer.write(code);
			writer.close();
			Process p = Runtime.getRuntime().exec(command);
			Killer killer = new Killer(p);
			killer.start();
			r.result = "";
			int k;
			InputStream error = p.getErrorStream();
			do {
				k = error.read();
				r.result += k >= 0 ? (char)k : "";
			} while (k != -1);
			
			InputStream in = p.getInputStream();
			do {
				k = in.read();
				r.result += k >= 0 ? (char)k : "";
			} while (k != -1);
		} catch (Exception e) {
			r.result = "Timeout";	
		} finally {
			File f = new File(name);
			f.delete();
		}
		return r;
	}

}

class Result {
	public String result;
}

class Killer extends Thread{
	Killer(Process p) {
		process = p;
	}
	Process process;
	public void run() {
		try {
			Thread.sleep(5000);
			process.destroy();
		} catch (Exception e) { }
	}
}