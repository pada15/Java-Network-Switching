package main.java.com.digitalroute;

import main.java.com.digitalroute.output.BillingGateway.*;

public class CDRs {

    String callId;
    int seqNum, aNum, bNum;
    int causeForOutput;
    int duration;
    ErrorCause error;

    public CDRs(String callId, int seqNum, int aNum, int bNum, int causeForOutput, int duration) {
        this.callId = callId;
        this.seqNum = seqNum;
        this.aNum = aNum;
        this.bNum = bNum;
        this.causeForOutput = causeForOutput;
        this.duration = duration;
    }

    public ErrorCause getError() {
        return error;
    }

    public void setError(ErrorCause error) {
        this.error = error;
    }
}
