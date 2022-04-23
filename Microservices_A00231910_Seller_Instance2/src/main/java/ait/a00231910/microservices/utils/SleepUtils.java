package ait.a00231910.microservices.utils;

public class SleepUtils {
	
	public static void sleep(int delayMs)
	{
		try
		{
			Thread.currentThread().sleep(delayMs);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
