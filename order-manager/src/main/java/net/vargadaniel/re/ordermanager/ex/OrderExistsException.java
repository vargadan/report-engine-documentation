package net.vargadaniel.re.ordermanager.ex;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_ACCEPTABLE, reason="Order already exists")
public class OrderExistsException extends Exception {

	private static final long serialVersionUID = 1L;

}