package com.xenaye.python.interpreter.controllers;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PythonInterpreterRestControllerTests {

	 @Autowired
	 private MockMvc mockMvc;
	 
	 @Test
	 public void execute_instruction_OK() throws Exception {
		 
		 MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		 String instruction = "{\"code\": \"%python print(1+1) \"}";
		 
	     mockMvc.perform(get("/execute")  		 			
	    		 			.param("sessionId", "piu1")
	    		 			.contentType(mediaType)
	    		 			.content(instruction)
	    		 			.accept(mediaType))
                .andExpect(content().contentType(mediaType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("2")));
	 }
	 
	 @Test
	 public void user_piu1_define_a_variable() throws Exception {
		 
		 MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		 String instruction = "{\"code\": \"%python a = 11 \"}";
		 
	     mockMvc.perform(get("/execute")  		 			
	    		 			.param("sessionId", "piu1")
	    		 			.contentType(mediaType)
	    		 			.content(instruction)
	    		 			.accept(mediaType))
                .andExpect(content().contentType(mediaType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("")));
	 }
	 
	 @Test
	 public void user_piu1_defined_variable_is_saved() throws Exception {
		 
		 MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		 String instruction = "{\"code\": \"%python a + 1 \"}";
		 
	     mockMvc.perform(get("/execute")  		 			
	    		 			.param("sessionId", "piu1")
	    		 			.contentType(mediaType)
	    		 			.content(instruction)
	    		 			.accept(mediaType))
                .andExpect(content().contentType(mediaType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("12")));
	 }
	 
	 @Test
	 public void user_piu3_can_not_access_piu1_session() throws Exception {
		 
		 MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		 String instruction = "{\"code\": \"%python a \"}";
		 
	     mockMvc.perform(get("/execute")  		 			
	    		 			.param("sessionId", "piu3")
	    		 			.contentType(mediaType)
	    		 			.content(instruction)
	    		 			.accept(mediaType))
                .andExpect(content().contentType(mediaType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("NameError: name 'a' is not defined")));
	 }
	 
	 @Test
	 public void the_code_cannot_be_parsed() throws Exception {
		 
		 MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		 String instruction = "{\"code\": \"%python random string \"}";
		 
	     mockMvc.perform(get("/execute")  		 			
	    		 			.param("sessionId", "piu1")
	    		 			.contentType(mediaType)
	    		 			.content(instruction)
	    		 			.accept(mediaType))
                .andExpect(content().contentType(mediaType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("SyntaxError: invalid syntax")));
	 }
	
	 @Test
	 public void  the_type_of_interpreter_is_unknown() throws Exception {
		 
		 MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		 String instruction = "{\"code\": \"%javascript window.alert(5 + 6);\"}";
		 
	     mockMvc.perform(get("/execute")  		 			
	    		 			.param("sessionId", "piu1")
	    		 			.contentType(mediaType)
	    		 			.content(instruction)
	    		 			.accept(mediaType))
                .andExpect(content().contentType(mediaType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("interpreter is unknown")));
	 }
	 
}
