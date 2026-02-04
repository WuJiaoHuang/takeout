ThreadLocal是Thread的局部变量
为每个线程提供一份独立存储空间，具有线程隔离的效果
只有在线程内才能获取到对应的值，线程外则不能访问

public void set(T value) 设置当前线程的线程局部变量的值
public T get() 返回当前线程对应的线程局部变量的值
public void remove() 移除当前线程的线程局部变量