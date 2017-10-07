/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.digitalroute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import main.java.com.digitalroute.input.CallRecordsProcessor;
import main.java.com.digitalroute.output.BillingGateway;

public class YourImplementation implements CallRecordsProcessor {

    BillingGateway Bill;

    public YourImplementation(BillingGateway billingGateway) {
        this.Bill = billingGateway;
    }

    public String input(InputStream in) {
        BufferedReader br = null;
        String all = "";

        try {

            br = new BufferedReader(new InputStreamReader(in));

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                all += sCurrentLine + "\n";
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null) {
                    br.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return all;
    }

    public void print(ArrayList<CDRs> abc) {

        for (CDRs temp : abc) {
            System.out.print(temp.callId);
            System.out.print(",");
            System.out.print(temp.seqNum);
            System.out.print(",");
            System.out.print(temp.aNum);
            System.out.print(",");
            System.out.print(temp.bNum);
            System.out.print(",");
            System.out.print(temp.causeForOutput);
            System.out.print(",");
            System.out.println(temp.duration);
        }
    }

    @Override
    public void processBatch(InputStream in) {

        String rec = input(in);
        String[] cdr_list = rec.split("\n");
        ArrayList<CDRs> CDRs = new ArrayList<CDRs>();

        for (String cdr_temp : cdr_list) {
            String[] temStr = cdr_temp.split(":");

            String callId = temStr[0];
            temStr = temStr[1].split(",");
            CDRs.add(new CDRs(callId, Integer.parseInt(temStr[0]),
                    Integer.parseInt(temStr[1]),
                    Integer.parseInt(temStr[2]),
                    Integer.parseInt(temStr[3]),
                    Integer.parseInt(temStr[4])));
        }
        AggregationRule_3(CDRs);
    }

    public void AggregationRule_1(ArrayList<CDRs> cdrS_old, ArrayList<CDRs> logerror) {
        ArrayList<CDRs> cdrS_new = new ArrayList<>();
        CDRs tempCR;
        String callId;
        int seqNum, aNum, bNum, causeForOutput, duration;

        Iterator<CDRs> iterator = cdrS_old.iterator();
        while (iterator.hasNext()) {
            CDRs next = iterator.next();
            tempCR = next;
            iterator.remove();
            if (tempCR.causeForOutput != 2) {

                Iterator<CDRs> iterator_2 = cdrS_old.iterator();
                while (iterator_2.hasNext()) {
                    CDRs cdrtemp = iterator_2.next();

                    callId = cdrtemp.callId;
                    aNum = cdrtemp.aNum;
                    bNum = cdrtemp.bNum;

                    if (callId.equals(tempCR.callId) && aNum == tempCR.aNum
                            && bNum == tempCR.bNum) {
                        seqNum = cdrtemp.seqNum;
                        duration = cdrtemp.duration;
                        causeForOutput = cdrtemp.causeForOutput;

                        if (seqNum > tempCR.seqNum) {
                            tempCR.seqNum = seqNum;
                        }

                        tempCR.duration = tempCR.duration + duration;
                        iterator_2.remove();

                        if (causeForOutput == 2) {
                            tempCR.causeForOutput = causeForOutput;
                            break;
                        } else if (causeForOutput == 0) {
                            tempCR.causeForOutput = causeForOutput;
                        }

                    }
                }
            }

            //------------
            cdrS_new.add(tempCR);
            iterator = cdrS_old.iterator();
        }
//            print(cdrS_new);
        AggregationRule_2(cdrS_new, logerror);
    }

    public void AggregationRule_2(ArrayList<CDRs> cdrS_old, ArrayList<CDRs> logerror) {

        ArrayList<CDRs> cdrS_new = new ArrayList<>();
        CDRs tempCR;
        String callId;
        int seqNum, aNum, bNum, causeForOutput, duration;

        Iterator<CDRs> iterator = cdrS_old.iterator();
        while (iterator.hasNext()) {
            CDRs next = iterator.next();
            tempCR = next;
            iterator.remove();

            if (tempCR.causeForOutput != 2 && !tempCR.callId.equals("_")) {

                Iterator<CDRs> iterator_2 = cdrS_old.iterator();
                while (iterator_2.hasNext()) {
                    CDRs cdrtemp = iterator_2.next();

                    callId = cdrtemp.callId;
                    aNum = cdrtemp.aNum;
                    bNum = cdrtemp.bNum;

                    if (callId.equals("_") && aNum == tempCR.aNum
                            && bNum == tempCR.bNum) {
                        seqNum = cdrtemp.seqNum;
                        duration = cdrtemp.duration;
                        causeForOutput = cdrtemp.causeForOutput;

                        if (seqNum > tempCR.seqNum) {
                            tempCR.seqNum = seqNum;
                        }

                        tempCR.duration = tempCR.duration + duration;
                        iterator_2.remove();

                        if (causeForOutput == 2) {
                            break;
                        } else if (causeForOutput == 0) {
                            tempCR.causeForOutput = causeForOutput;
                        }
                    }
                }
            }

            //------------
            if (!tempCR.callId.equals("_")) {
                cdrS_new.add(tempCR);
            } else {
                tempCR.error = BillingGateway.ErrorCause.NO_MATCH;
                logerror.add(tempCR);
            }
            iterator = cdrS_old.iterator();
        }
//            print(cdrS_new);
        Bill(cdrS_new, logerror);
    }

    public void AggregationRule_3(ArrayList<CDRs> cdrS_old) {
        System.out.println("rule3");

        ArrayList<CDRs> cdrS_new = new ArrayList<>();
        ArrayList<CDRs> logError = new ArrayList<>();
        CDRs tempCR;
        String callId;
        int seqNum, aNum, bNum, causeForOutput, duration;

        Iterator<CDRs> iterator = cdrS_old.iterator();
        while (iterator.hasNext()) {
            CDRs next = iterator.next();
            tempCR = next;
            iterator.remove();

            Iterator<CDRs> iterator_2 = cdrS_old.iterator();
            while (iterator_2.hasNext()) {
                CDRs cdrtemp = iterator_2.next();
                seqNum = cdrtemp.seqNum;
                if (seqNum == tempCR.seqNum) {
                    cdrtemp.error = BillingGateway.ErrorCause.DUPLICATE_SEQ_NO;
                    logError.add(cdrtemp);
                    iterator_2.remove();
                }
            }
            //------------
            cdrS_new.add(tempCR);
            iterator = cdrS_old.iterator();
        }
        print(cdrS_new);
        AggregationRule_1(cdrS_new, logError);
    }

    public void Bill(ArrayList<CDRs> cdrS, ArrayList<CDRs> logError) {
        Bill.beginBatch();
        int totalDuration = 0;
        for (CDRs temp : cdrS) {
            totalDuration += temp.duration;
            Bill.consume(temp.callId, temp.seqNum, Integer.toString(temp.aNum), Integer.toString(temp.bNum), (byte) (temp.causeForOutput), temp.duration);
        }
        Bill.endBatch(totalDuration);
        for (CDRs temp : logError) {
            totalDuration += temp.duration;
            Bill.logError(temp.error, temp.callId, temp.seqNum, Integer.toString(temp.aNum), Integer.toString(temp.bNum));
        }

    }
}
