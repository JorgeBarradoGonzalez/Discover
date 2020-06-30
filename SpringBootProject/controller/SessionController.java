package jb.dam2.discover.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.service.ISessionService;

/*JB:
 * Controlador para las sesiones
 */
@RestController
@RequestMapping(value = "/sessions")
public class SessionController {
	
	@Autowired
	private ISessionService sessionService;
	
	@GetMapping(value="/all") 
	public List<Session> getAllCustomer(){return sessionService.findAll();}
}
