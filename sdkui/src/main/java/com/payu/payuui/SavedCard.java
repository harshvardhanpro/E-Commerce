package com.payu.payuui;

import android.widget.ImageView;

/**
 * Created by piyushkhandelwal on 9/9/15.
 */
public class SavedCard {
    private String maskedCardNumber;
    private String cardName;
    private String cardType;
    private ImageView cardTypeImageSource;
    private ImageView bankImageSource;

    public ImageView getCardTypeImageSource() {
        return cardTypeImageSource;
    }

    public void setCardTypeImageSource(ImageView cardTypeImageSource) {
        this.cardTypeImageSource = cardTypeImageSource;
    }

    public ImageView getBankImageSource() {
        return bankImageSource;
    }

    public void setBankImageSource(ImageView bankImageSource) {
        this.bankImageSource = bankImageSource;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }



    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }
}
