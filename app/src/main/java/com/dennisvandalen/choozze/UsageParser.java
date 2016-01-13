package com.dennisvandalen.choozze;

import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsageParser {
    private String type;
    private String sms;
    private String call;
    private String internet;
    private String other;
    private String costs;

    public UsageParser(Document document) {
        type = document.select("body > div.container > div > p:nth-child(4)").text();
        sms = document.select("body > div.container > div > div:nth-child(7) > div.col-xs-12.col-md-8").text();
        call = document.select("body > div.container > div > div:nth-child(8) > div.col-xs-12.col-md-8").text();
        internet = document.select("body > div.container > div > div:nth-child(9) > div.col-xs-12.col-md-8").text();
        other = document.select("body > div.container > div > div:nth-child(10) > div.col-xs-12.col-md-8").text();
        costs = document.select("body > div.container > div > p:nth-child(14)").text();
    }

    protected String getCosts() {
        String re1 = ".*?";    // Non-greedy match on filler
        String re2 = "(\\d+)";    // Integer Number 1
        String re3 = "(,)";    // Any Single Character 1
        String re4 = "(\\d+)";    // Integer Number 2

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(costs);
        if (m.find()) {
            String int1 = m.group(1);
            String c1 = m.group(2);
            String int2 = m.group(3);

            costs = String.format("€%s%s%s", int1, c1, int2);
        }

        return costs;
    }

    public String getType() {
        return type.replace("Jij hebt gekozen voor ", "");
    }

    public String getOther() {
        return other.replace("(", "\n(");
    }

    public String[] getCall() {
        String re1 = "(\\d+)";    // Integer Number 1
        String re2 = ".*?";    // Non-greedy match on filler
        String re3 = "(\\d+)";    // Integer Number 2

        String callTotal = "";
        String callUsed = "";

        Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(call);
        if (m.find()) {
            callTotal = m.group(1);
            callUsed = m.group(2);
        }

        return new String[]{callTotal, callUsed};
    }

    public String[] getSms() {
        String re1 = "(\\d+)";    // Integer Number 1
        String re2 = ".*?";    // Non-greedy match on filler
        String re3 = "(\\d+)";    // Integer Number 2

        String smsTotal = "";
        String smsUsed = "";

        Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(sms);
        if (m.find()) {
            smsTotal = m.group(1);
            smsUsed = m.group(2);
        }

        return new String[]{smsTotal, smsUsed};
    }

    public String[] getInternet() {
        String internetTotal = "";
        String internetUsed = "";

        String re1 = "(\\d+)";    // Integer Number 1
        String re2 = "((?:[a-z][a-z]+))";    // Word 1
        String re3 = ".*?";    // Non-greedy match on filler
        String re4 = "(\\d+)";    // Integer Number 2

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(internet);
        if (m.find()) {
            internetTotal = m.group(1) + m.group(2);
            internetUsed = m.group(3);
        }

        return new String[]{internetTotal, internetUsed};
    }
}
