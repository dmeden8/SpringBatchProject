package syncer.batch.core;

public class LightspeedHelper {
	
	public static int getTimeToWaitBeforeSendingRequets(String rate, int unitsPerRequest) {
		
		int timeToWait = 0;
		
		if (rate == null) {
			return 0;
		}
										
		double currentBucketSize = Double.parseDouble(rate.substring(0,rate.indexOf("/")));		
		double totalBucketSize = Double.parseDouble(rate.substring(rate.indexOf("/")+1));
		
		double dripRate = totalBucketSize/60;		
		double bucketFreeSpace = totalBucketSize - currentBucketSize;			
		
		if (unitsPerRequest == 1 && bucketFreeSpace < 1/dripRate) {
			timeToWait = (int) (1000 / dripRate) + 1;
		}
		else if (unitsPerRequest == 10 && bucketFreeSpace <  10/dripRate) {
			timeToWait = (int) (10000 / dripRate) + 1;
		}
								
		return timeToWait;
		
	}

}
