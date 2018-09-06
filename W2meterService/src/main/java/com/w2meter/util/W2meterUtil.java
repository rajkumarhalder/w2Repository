package com.w2meter.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.w2meter.dto.AppInfo;
import com.w2meter.entity.VoteDetails;

public class W2meterUtil {
	
	public static Object getStatistics(AppInfo info,List<VoteDetails> listOfVoteDetails) {
		
		BigDecimal percentage=null;
		try {
			BigDecimal myVote=new BigDecimal(0);
			BigDecimal othersVote=new BigDecimal(0);

			int noOfOthers=0;
			for (VoteDetails voteDetails : listOfVoteDetails) {
				if(voteDetails.getUserId()==info.getUserId())
					myVote=new BigDecimal(voteDetails.getVoteValue());
				othersVote=othersVote.add(new BigDecimal(voteDetails.getVoteValue()));
				noOfOthers++;
			}

			BigDecimal othersVoteavg=othersVote.divide(new BigDecimal(noOfOthers),2, RoundingMode.HALF_UP);

			percentage=myVote.divide(othersVoteavg,2, RoundingMode.HALF_UP);
			percentage=percentage.subtract(new BigDecimal(1));
			percentage=percentage.multiply(new BigDecimal(100));

		} catch (Exception e) {
			throw e;
		}
		return percentage;
	}

	
	
	
	
}
