package br.gov.sc.epagri.core.utils;

import java.util.function.Consumer;

import org.jboss.logging.Logger;

/**
 * @author João Junior DATA52849
 * */


@FunctionalInterface
public interface ThrowingFunction<T, E extends Exception> {
	
	void apply(T t) throws E;
	
	static <T, E extends Exception> Consumer<T> consumer(ThrowingFunction<T, E> throwingConsumer) {
			  	Logger logger = Logger.getLogger(ThrowingFunction.class);
			    return i -> {
			        try {
			            throwingConsumer.apply(i);
			        } catch (Exception ex) {
			            try {
			                logger.error(ex);
			            } catch (ClassCastException ccEx) {
			                throw new RuntimeException(ex);
			            }
			        }
			    };
			}
}
