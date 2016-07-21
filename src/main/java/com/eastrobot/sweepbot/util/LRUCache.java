package com.eastrobot.sweepbot.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("unchecked")
public class LRUCache {
	
	private final ReentrantReadWriteLock theLock = new ReentrantReadWriteLock();
	private final Lock writeLock = theLock.writeLock();
	private final Lock readLock = theLock.readLock();
	
	private int cacheSize = 0;  
	
	private LinkedHashMap map;  
    
	public LRUCache(int cacheSize)  
	{  
		this.cacheSize = cacheSize;  
		this.map = new LinkedHashMap(cacheSize, 1f, true)  
		{  
			private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(Map.Entry eldest)  
			{  
				return size() > LRUCache.this.cacheSize;  
			}  
		};  
	}  
	
	public Object get(Object key) {
		readLock.lock();
		try {
			return map.get(key);
		} finally {
			readLock.unlock();
		}
	}
	public Object retrieve(Object key) {
		return get(key);
	}
	public Object put(Object key,Object value) {
		writeLock.lock();
		try {
			return map.put(key,value);
		} finally {
			writeLock.unlock();
		}
	}
	public Object store(Object key,Object value) {
		return put(key,value);
	}
	public Object remove(Object key) {
		writeLock.lock();
		try {
			return map.remove(key);
		} finally {
			writeLock.unlock();
		}
	}
	public int size() {
		readLock.lock();
		try {
			return map.size();
		} finally {
			readLock.unlock();
		}
	}
	
	public String printDetail() {
		String ret = "[";
		for (Object obj : map.entrySet())  
		{  
			Map.Entry entry = (Map.Entry)obj;
			ret = ret + entry.getKey()+"="+entry.getValue()+",";
		} 
		ret = ret+"]";
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		LRUCache cache = new LRUCache(4);
		cache.put("a", "a");
		cache.put("b", "b");
		cache.put("c", "c");
		cache.get("a");
		cache.put("d", "d");

		System.out.println(cache.printDetail());
		
	}
}
