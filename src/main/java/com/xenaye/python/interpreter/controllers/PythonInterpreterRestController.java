package com.xenaye.python.interpreter.controllers;

import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class PythonInterpreterRestController {

    @RequestMapping(method = RequestMethod.GET, value="/execute")
    public Map<String, String> execute(@RequestBody Map<String, String> codeMap,
    									@RequestParam("sessionId") String sessionId) throws IOException {
    	
    	String line;
    	String errorMessage = "";
    	String result = "";
    	String code = codeMap.get("code");
    	Map<String, String> resultMap = Collections.singletonMap("result", result);
    	
    	//a link to local files that save users data, if a new user create new file
    	Path path = Paths.get("src//main//resources//python//"+ sessionId);
    	if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) Files.createFile(path);
    		
    	//if the code does not start with "%python" return an error message 
    	if(!code.startsWith("%python")) {
    		return Collections.singletonMap("error", "interpreter is unknown");
    	}
    	
    	//removing the "%python" from the received code
    	code = code.substring(8);
    	
    	//new process to run the python process
    	ProcessBuilder processBuilder = new ProcessBuilder();
    	processBuilder.command("cmd.exe", "/C", "python", "-i");
    	try {
    		
			Process process = processBuilder.start();
			
			//before running the user command, run saved commands (variable definitions) he run previously 
			OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
			for(String ln : Files.readAllLines(path)) {
				osw.write(ln + "\n");
            }
			osw.write(code);
			osw.close();
			
			//check if the python process returned an error, if yes return a json error message 
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
            	if(line.contains("Error")) errorMessage = line;	
            }
            if(!errorMessage.equals(""))
            {
            	resultMap = Collections.singletonMap("error", errorMessage);
                return resultMap;
            }
            
            // save the python process response to result string
            BufferedReader resultReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = resultReader.readLine()) != null) {
            	result = line;
            }
            
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
            
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	//save new variables definition to the user file
    	if(result.equals("")){
	    	code = code + "\n";
	    	Files.write(path, code.getBytes(), StandardOpenOption.APPEND); 
    	}
    	
    	//return the result response 
    	resultMap = Collections.singletonMap("result", result);
        return resultMap;
    }

}