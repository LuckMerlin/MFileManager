package com.csdk.ui.ue4;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Create by liao on 2020/12/2
 */
 final class JReflectUtil {

	static Method m_forName = null;
	static Method m_getDeclaredField = null;
	static Method m_getSuperclass = null;
	static Method m_getDeclaredMethod = null;

	static {
		m_forName = findMethodInner(Class.class, "forName", String.class);
		m_getDeclaredField = findMethodInner(Class.class, "getDeclaredField", String.class);
		m_getSuperclass = findMethodInner(Class.class, "getSuperclass");
		m_getDeclaredMethod = findMethodInner(Class.class, "getDeclaredMethod", String.class, Class[].class);
	}

	protected static Method findMethodInner(Class clz, String name, Class<?>... parameterType) {
		while (clz != null) {
			try {
				Method m1 = clz.getDeclaredMethod(name, parameterType);
				if (m1 != null) {
					m1.setAccessible(true);
					return m1;
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}

			clz = clz.getSuperclass();
		}
		return null;
	}

	public static Class<?> forName(String className) throws ClassNotFoundException {
		if (m_forName != null) {
			try {
				return (Class<?>) m_forName.invoke(Class.class, className);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return Class.forName(className);
	}

	public static Object tryInvoke(Method m, Object obj, Object... arg) {
		try {
			return m.invoke(obj, arg);
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			//e.printStackTrace();
		}
		return null;
	}

	public static Field findField(Class<?> clz, String name) {
		//return clz.getDeclaredField(name);

		if (m_getDeclaredField != null) {
			while (clz != null) {
				Object f1 = tryInvoke(m_getDeclaredField, clz, name);
				if (f1 instanceof Field) {
					((Field) f1).setAccessible(true);
					return (Field) f1;
				}
				if (m_getSuperclass != null) {
					clz = (Class<?>) tryInvoke(m_getSuperclass, clz);
				} else {
					clz = clz.getSuperclass();
				}
			}
		} else {
			while (clz != null) {
				try {
					Field f1 = clz.getDeclaredField(name);
					if (f1 != null) {
						f1.setAccessible(true);
						return f1;
					}
				} catch (NoSuchFieldException e) {
					//e.printStackTrace();
				}
				clz = clz.getSuperclass();
			}
		}
		return null;
	}

	public static Method findMethod(Class<?> clz, String name, Class<?>... parameterType) {
		//return clz.getDeclaredMethod(name, parameterType);

		if (m_getDeclaredMethod != null) {
			while (clz != null) {
				Object m1 = tryInvoke(m_getDeclaredMethod, clz, name, parameterType);
				if (m1 instanceof Method) {
					((Method) m1).setAccessible(true);
					return (Method) m1;
				}
				if (m_getSuperclass != null) {
					clz = (Class<?>) tryInvoke(m_getSuperclass, clz);
				} else {
					clz = clz.getSuperclass();
				}
			}
		} else {
			while (clz != null) {
				try {
					Method m1 = clz.getDeclaredMethod(name, parameterType);
					if (m1 != null) {
						m1.setAccessible(true);
						return m1;
					}
				} catch (NoSuchMethodException e) {
					//e.printStackTrace();
				}
				clz = clz.getSuperclass();
			}
		}
		return null;
	}

}
