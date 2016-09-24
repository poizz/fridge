package gcm;

import java.io.IOException;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class SendGCM {
	String apiKey = "AIzaSyCtYd0AnqvSukw7xNTryyKd7zXRexb3PMM";

	public void sendGCMMsg(String msg,String taskCode, String registrationId) throws IOException {
		Sender sender = new Sender(apiKey);
		Message message = new Message.Builder().collapseKey("FirebaseMessage").timeToLive(10).delayWhileIdle(false).addData("taskCode", taskCode).addData("message", msg).build();

		Result result = sender.send(message, registrationId, 5);	
		System.out.println("Message ID:"+result.getMessageId());
		System.out.println("Failed:" + result.getErrorCodeName());
		System.out.println("regid: "+registrationId);
		System.out.println("msg send via firebase! "+msg+" "+result.getErrorCodeName());
	}
				
}

