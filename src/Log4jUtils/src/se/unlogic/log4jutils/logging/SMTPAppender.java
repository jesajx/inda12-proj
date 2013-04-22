package se.unlogic.log4jutils.logging;


public class SMTPAppender extends org.apache.log4j.net.SMTPAppender {

	private static final AllTriggeringEventEvaluator EVENT_EVALUATOR = new AllTriggeringEventEvaluator();
	
	public SMTPAppender(){
		
		setEvaluator(EVENT_EVALUATOR);
	}
}
