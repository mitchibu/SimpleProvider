package jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	ColumnType type() default ColumnType.TEXT;
	boolean unique() default false;
	boolean isNull() default true;
	boolean primaryKey() default false;
	boolean autoIncrement() default false;
	String defaultValue() default "";
}
