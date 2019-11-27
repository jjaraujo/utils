package br.gov.sc.epagri.core.utils;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

/**
 * @author João Junior DATA52849
 * */

public final class CopiaObjeto {
	
	private static Logger LOGGER = Logger.getLogger(CopiaObjeto.class);
			
	private CopiaObjeto(){
		
	}
	
	/**
	 * Insere os valores de todos os atributos do @param objOld em uma nova instância da
	 * classe @param newClass. É considerado que os metodos acessores estão nomeados conforme convenção.
	 * Ex: se um atributo se chama obj, seus métodos acessores serão setObj e getObj, ou isObj, caso o tipo seja boolean.
	 * */
	public static <T> T clone(Object objOld, Class<T> newClass) throws IntrospectionException, ReflectiveOperationException{
		T newObj = newClass.newInstance();
		clone(objOld, newObj);
		return newObj;
	}
	
	/**
	 * Insere os valores de todos os atributos do @param objOld no objeto @param newObj. É considerado que os metodos acessores estão nomeados conforme convenção.
	 * Ex: se um atributo se chama obj, seus métodos acessores serão setObj e getObj, ou isObj, caso o tipo seja boolean.
	 * */
	public static <T> void clone(Object objOld, T newObj) throws IntrospectionException, ReflectiveOperationException{
		List<Field> fields = getListFields(objOld.getClass());
		List<String> listNameNewFields = getListNameFields(newObj.getClass());
		for(Field f : fields){
			String nameFiled = f.getName();
			if(listNameNewFields.contains(nameFiled)){
				setValue(f, objOld, newObj);
			}
		}
	}
	
	/**
	 * 
	 * */
	private static List<String> getListNameFields(Class<?> newClass){
		List<String> list = new ArrayList<>();
		for(Field f : newClass.getDeclaredFields()){
			list.add(f.getName());
		}
		Class<?> superClass = newClass.getSuperclass();
		if(!superClass.getSimpleName().equals("Object")){
			list.addAll(getListNameFields(superClass));
		}
		return list; 
	}
	
	/**
	 * Retorna uma lista dos dos atributos da classe informada, 
	 * incluindo os atributos das classes herdadas.
	 * */
	private static List<Field> getListFields(Class<?> obj){
		List<Field> list = new ArrayList<>();
		Class<?> superClass = obj.getSuperclass();
		for(Field f : obj.getDeclaredFields()){
			list.add(f);
		}
		if(!superClass.getSimpleName().equals("Object")){
			list.addAll(getListFields(superClass));
		}
		return list;
	}
	
	/**
	 * Seta o valor do field @param f disponível no @param objOld no novo objeto @param newObj 
	 * */
	private static void setValue(Field f, Object objOld, Object newObj) throws IntrospectionException, ReflectiveOperationException {
		try {
			PropertyDescriptor desc = new PropertyDescriptor(f.getName(), newObj.getClass());
			Object value = getValue(f, objOld);
			desc.getWriteMethod().invoke(newObj, value);
		} catch (InvocationTargetException | 
												IllegalAccessException e) {
			throw e;
		}catch (IntrospectionException e) {
			if(!f.getName().equals("serialVersionUID")){
				LOGGER.error(e.getMessage());
			}
		}catch (IllegalArgumentException e) {
			LOGGER.error("Atributo com mesmo nome mas de tipos diferentes: " + f.getName());
		}
	}
	
	/**
	 * Busca o valor do field @param f dentro do @param obj
	 * */
	private static Object getValue(Field f, Object obj) throws InvocationTargetException, IntrospectionException{
		try {
			String name = getBaseName(f.getName());
			String readName = f.getType().isInstance(Boolean.TYPE) ? "is" : "get";
			PropertyDescriptor desc = new PropertyDescriptor(f.getName(), obj.getClass(), readName+name,"set"+name);
			return desc.getReadMethod().invoke(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new InvocationTargetException(e);
		} catch (IntrospectionException e) {
			if(!f.getName().equals("serialVersionUID")){
				throw new IntrospectionException(e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * Retorna a string com a primeira letra maiuscula
	 * */
	private static String getBaseName(String atributteName){
		return atributteName.substring(0,1).toUpperCase().concat((atributteName
					.substring(1, atributteName.length())));
	}
}
