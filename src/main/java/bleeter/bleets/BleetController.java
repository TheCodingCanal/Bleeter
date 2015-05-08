package bleeter.bleets;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bleeter.users.BleetUser;
import bleeter.users.UserServices;

@Controller
public class BleetController {
	@Autowired
	private UserServices userServices;
	@Autowired
	private BleetServices bleetServices;

	@RequestMapping("/bleets")
	@ResponseBody
	public Page<Bleet> getBleets(
			@RequestParam(required = false, defaultValue="0") Integer page,
			@RequestParam(required = false, defaultValue="username") String sort,
			@RequestParam(required = false, defaultValue="ASC") String order) {
		Sort s;
		if (order.equals("ASC")) {
			s = new Sort(Direction.ASC, sort);
		}
		else {
			s = new Sort(Direction.DESC, sort);
		}
		if (page < 0)
			page = 0;
		return bleetServices.findAllBleets(page, s);
	}
	
	@RequestMapping("users/{uid}")
	@ResponseBody
	@PreAuthorize(value = "principal.id == #uid")		
	public BleetUser getUser(@PathVariable String uid) {
		return userServices.findById(uid);
	}
	
	@RequestMapping(value = "users/{uid}", method = RequestMethod.PUT)
	@ResponseBody
	@PostAuthorize(value = "principal.id == #uid")
	public BleetUser updateUser(@PathVariable String uid, @RequestParam String firstname,
			@RequestParam String lastname, @RequestParam String username, 
			@RequestParam String email) {
		BleetUser user = userServices.findByUsername(uid);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setUsername(username);
		user.setEmail(email);
		return userServices.updateUser(user);
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "users/{uid}/authorities", method = RequestMethod.PUT)
	@ResponseBody
	public Page<BleetUser> changeAdmin(@PathVariable String uid,
			@RequestParam(required = false, defaultValue="0") Integer page) {
		return userServices.changeAdmin(uid, page);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/bleets/{bid}/blck", method = RequestMethod.PUT)
	@ResponseBody
	public Page<Bleet> changeBlock(@PathVariable String bid,
			@RequestBody Boolean block,
			@RequestBody Integer page,
			@RequestBody String sort,
			@RequestBody String order) {
		Sort s;
		if (order.equals("ASC")) {
			s = new Sort(Direction.ASC, sort);
		}
		else {
			s = new Sort(Direction.DESC, sort);
		} 
		return bleetServices.changeBlock(bid, page, s, block);
	}
	
	/*@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/bleets")
	@ResponseBody
	public List<Bleet> searchBleets(@RequestParam(required=false, defaultValue="") String username,
			@RequestParam(required=false, defaultValue="01/01/2999") Date timestamp) {
		return userServices.searchBleets(username, timestamp);
	}*/

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "users", method = RequestMethod.POST)
	@ResponseBody
	public Page<BleetUser> createUser(@RequestParam String username,
			@RequestParam String password,
			@RequestParam String firstname,
			@RequestParam String lastname,
			@RequestParam String email,
			@RequestParam(required = false, defaultValue="0") Integer page) {
		BleetUser newUser = new BleetUser.Builder().username(username).password(password)
				.firstName(firstname).lastName(lastname).email(email).build();
		return userServices.createUser(newUser, page);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "users", method = RequestMethod.GET)
	@ResponseBody
	public Page<BleetUser> getUsers(
			@RequestParam(required = false, defaultValue="0") Integer page){			
		return userServices.findAllUsers(page);
	}

	@Secured("ROLE_USER")
	@RequestMapping("users/{uid}/bleets")
	@ResponseBody
	@PreAuthorize(value = "principal.id == #uid")
	public Page<Bleet> getUsersBleets(@PathVariable String uid,
			@RequestParam(required = false, defaultValue="0") Integer page,
			@RequestParam(required = false, defaultValue="username") String sort,
			@RequestParam(required = false, defaultValue="ASC") String order) {
		Sort s;
		if (order.equals("ASC")) {
			s = new Sort(Direction.ASC, sort);
		}
		else {
			s = new Sort(Direction.DESC, sort);
		}
		if (page < 0)
			page = 0;
		return bleetServices.findBleets(uid, page, s);
	}
	
	/*@Secured("ROLE_USER")
	@RequestMapping("users/{uid}/bleets/{bid}")
	@ResponseBody
	@PreAuthorize(value = "principal.id == #uid")		
	public Bleet getBleet(@PathVariable String uid, @PathVariable String bid) {
		return bleetServices.findByBleetId(uid, bid);
	}*/
	
	@Secured("ROLE_USER")
	@RequestMapping(value = "users/{uid}/bleets/{bid}", method = RequestMethod.DELETE)
	@ResponseBody
	@PreAuthorize("principal.id == #uid")		
	public Page<Bleet> deleteBleet(@PathVariable String uid, @PathVariable String bid) {	
		return bleetServices.deleteBleet(uid, bid);
	}	
	
	@Secured("ROLE_USER")
	@RequestMapping(value = "users/{uid}/bleets", method = RequestMethod.POST)
	@ResponseBody
	@PostAuthorize(value = "principal.id == #uid")		
	public Page<Bleet> createBleet(@PathVariable String uid,
			@RequestParam String bleet,
			@RequestParam Boolean privatecomment) {
		BleetUser user = userServices.findById(uid);
		Bleet newBleet = new Bleet.Builder().bleet(bleet).privateComment(privatecomment)
				.uid(uid).username(user.getUsername()).sentiment("").timestamp(new Date())
				.confidence(0).build();
		return bleetServices.addBleet(uid, newBleet);
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(value = "users/{uid}/bleets/{bid}", method = RequestMethod.PUT)
	@ResponseBody
	@PostAuthorize(value = "principal.id == #uid")
	public Page<Bleet> updateBleet(@PathVariable String uid, @PathVariable String bid,
			@RequestParam String bleet, @RequestParam Boolean privatecomment){
		return bleetServices.updateBleet(uid, bid, bleet, privatecomment);
	}
	
}
