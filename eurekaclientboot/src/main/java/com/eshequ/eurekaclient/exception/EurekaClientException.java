package com.eshequ.eurekaclient.exception;

public class EurekaClientException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1452461224913194338L;

	/**
     * Constructs a {@code EurekaClientNotExistsException} with no detail message.
     */
    public EurekaClientException() {
        super();
    }
 
    /**
     * Constructs a {@code EurekaClientNotExistsException} with the specified
     * detail message.
     *
     * @param   message   the detail message.
     */
    public EurekaClientException(String message) {
        super(message);
    }
 
	
	
}
