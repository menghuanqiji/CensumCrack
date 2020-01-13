package com.censum.crack;

/**
 * @author TY
 */

public enum CanLoadState {

    SUCCESS("Your license will expire after %1$te/%1$tm/%1$tY", "License Checked"),
    LICENSE_EXPIRED("Your license has expired, please contact jClarity for a new license", "License Expired"),
    LICENSE_SOON_TO_EXPIRE("Your license will expire soon.  Censum will not be useable after %1$te/%1$tm/%1$tY", "License Expiring Soon");

    private final String body;
    private final String title;

    CanLoadState(String body, String title) {
        this.body = body;
        this.title = title;
    }


    public String getBody() { return this.body; }



    public String getTitle() { return this.title; }

}
