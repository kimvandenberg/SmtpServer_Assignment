package nl.avans.smtpstatemachine;

import nl.avans.SmtpContext;

public class ReceivingDataState implements SmtpStateInf {
    SmtpContext context;
    boolean crReceived;
    boolean dotReceived;

    public ReceivingDataState(SmtpContext context) {
        this.context = context;
    }

    @Override
    public void Handle(String data) {
        if(data != null) {
            context.AddTextToBody(data);
        }
        if(context.getTextBody() != null) {
            StringBuilder sb;
            sb = context.getTextBody();
            // "\r\n.\r\n" has 9 characters
            String lastLine = sb.substring(sb.length()-9);
            if(lastLine.equals("\r\n.\r\n")) {
                context.SendData("250 Ok: queued as 12345");
                context.SetNewState(new WaitForMailFromState(context));
                return;
            }
        }
        if(data.toUpperCase().startsWith("QUIT")) {
            context.SendData("221 Bye");
            context.DisconnectSocket();
            return;
        }
        context.SendData("503 Error...");
        //handle the receiving of the mailbody
        // "\r\n.\r\n" should return to the WaitForMailFromState
    }
}
