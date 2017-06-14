package com.payu.payuui.Fragment;

/**
 * Created by ankur on 8/24/15.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuUtils;
import com.payu.payuui.R;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;

import java.util.HashMap;


public final class SavedCardItemFragment extends Fragment {

    private StoredCard mStoredCard;
    private PayuUtils mPayuUtils;
    private EditText cvvEditText;
    private String issuingBankStatus;
    private TextView issuingBankDownText;
    private TextView cvvTextView;
    private Boolean oneClickPayment;
    private ViewPager mViewPager;
    private CheckBox enableOneClickPayment;
    private int position;
    private Boolean isCvvValid = false;

    HashMap<String, String> oneClickCardTokens;

    public SavedCardItemFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mStoredCard = bundle.getParcelable(PayuConstants.STORED_CARD);
        issuingBankStatus = bundle.getString(SdkUIConstants.ISSUING_BANK_STATUS);
        oneClickPayment = bundle.getBoolean(PayuConstants.ONE_CLICK_PAYMENT);
        position = bundle.getInt(SdkUIConstants.POSITION);
        oneClickCardTokens = (HashMap<String, String>) bundle.getSerializable(PayuConstants.ONE_CLICK_CARD_TOKENS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_saved_card, null);

        mViewPager = (ViewPager) container;

        issuingBankDownText = (TextView) view.findViewById(R.id.text_view_saved_card_bank_down_error);

        mPayuUtils = new PayuUtils();

        cvvEditText = (EditText) view.findViewById(R.id.edit_text_cvv);
        enableOneClickPayment = (CheckBox) view.findViewById(R.id.check_box_save_card_enable_one_click_payment);
        cvvTextView = (TextView) view.findViewById(R.id.cvv_text_view);

//        saveCvvLinearlayout = (LinearLayout) view.findViewById(R.id.layout_save_cvv_checkbox);

        if (mStoredCard.getCardBrand().equals("AMEX")) {
            cvvEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        }

        if (mStoredCard.getEnableOneClickPayment() == 1 && mStoredCard.getOneTapCard() == 1 ) {

            cvvEditText.setVisibility(View.GONE);
            enableOneClickPayment.setVisibility(View.GONE);
            cvvTextView.setText("Click Pay Now to Pay through this card");
        }else {
            enableOneClickPayment.setVisibility(View.VISIBLE);

        }

        if (mStoredCard.getMaskedCardNumber().length() == 19 && mStoredCard.getCardBrand() == "SMAE") {
            cvvEditText.setVisibility(View.GONE);
        }

        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String cvv = s.toString();

                ViewPager activityViewPager = (ViewPager) getActivity().findViewById(R.id.pager);

                if (position == mViewPager.getCurrentItem() && activityViewPager.getCurrentItem() == 0) {// hardcoded 0, try to remove it
                    if ((mPayuUtils.validateCvv(mStoredCard.getCardBin(), cvv) && !cvv.equals("")) || cvvEditText.getVisibility() == View.GONE) {
                        getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
                        isCvvValid = true;

                    } else {

                        getActivity().findViewById(R.id.button_pay_now).setEnabled(false);
                        isCvvValid = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((TextView) view.findViewById(R.id.text_view_masked_card_number)).setText(mStoredCard.getMaskedCardNumber());
        ((TextView) view.findViewById(R.id.text_view_card_name)).setText(mStoredCard.getCardName());
        ((TextView) view.findViewById(R.id.text_view_card_mode)).setText(mStoredCard.getCardMode());
        ((ImageView) view.findViewById(R.id.card_type_image)).setImageResource(getIssuerImage(mStoredCard.getCardBrand()));
        if(getIssuingBankImage(mStoredCard.getIssuingBank()) != 0)
            ((ImageView) view.findViewById(R.id.bank_image)).setImageResource(getIssuingBankImage(mStoredCard.getIssuingBank()));
        if (issuingBankStatus.equals("") == false) {
            issuingBankDownText.setVisibility(View.VISIBLE);
            issuingBankDownText.setText(issuingBankStatus);
        } else {
            issuingBankDownText.setVisibility(View.GONE);
        }


        return view;
    }


    static Drawable getDrawableUI(Context context,int resID) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
            return context.getResources().getDrawable(resID);
        } else {
            return context.getResources().getDrawable(resID, context.getTheme());
        }
    }

    private int getIssuingBankImage(String issuingBank) {
        
        int resID = getResources().getIdentifier(issuingBank.toLowerCase(), "drawable", getActivity().getPackageName());

        if (resID == 0)
            return 0;
        else
            return resID;

    }

    private int getIssuerImage(String issuer) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            switch (issuer) {
                case PayuConstants.VISA:
                    return R.drawable.logo_visa;
                case PayuConstants.LASER:
                    return R.drawable.laser;
                case PayuConstants.DISCOVER:
                    return R.drawable.discover;
                case SdkUIConstants.MAESTRO:
                    return R.drawable.mas_icon;
                case PayuConstants.MASTERCARD:
                    return R.drawable.mc_icon;
                case PayuConstants.AMEX:
                    return R.drawable.amex;
                case PayuConstants.DINR:
                    return R.drawable.diner;
                case PayuConstants.JCB:
                    return R.drawable.jcb;
                case PayuConstants.SMAE:
                    return R.drawable.maestro;
                case PayuConstants.RUPAY:
                    return R.drawable.rupay;
            }
            return 0;
        } else {

            switch (issuer) {
                case PayuConstants.VISA:
                    return R.drawable.logo_visa;
                case PayuConstants.LASER:
                    return R.drawable.laser;
                case PayuConstants.DISCOVER:
                    return R.drawable.discover;
                case SdkUIConstants.MAESTRO:
                    return R.drawable.mas_icon;
                case PayuConstants.MASTERCARD:
                    return R.drawable.mc_icon;
                case PayuConstants.AMEX:
                    return R.drawable.amex;
                case PayuConstants.DINR:
                    return R.drawable.diner;
                case PayuConstants.JCB:
                    return R.drawable.jcb;
                case PayuConstants.SMAE:
                    return R.drawable.maestro;
                case PayuConstants.RUPAY:
                    return R.drawable.rupay;
            }
            return 0;
        }
    }


    public String getCvv() {
        return cvvEditText.getText().toString();
    }

    public Boolean isEnableOneClickPaymentChecked() {
        return enableOneClickPayment.isChecked();
    }

    public Boolean cvvValidation() {
        String cvv = "";
        PayuUtils myPayuUtils = new PayuUtils();
        if (cvvEditText != null && cvvEditText.getText() != null) {
            cvv = cvvEditText.getText().toString();

            if ((myPayuUtils.validateCvv(mStoredCard.getCardBin(), cvv) && !cvv.equals("")) || (cvvEditText != null && cvvEditText.getVisibility() == View.GONE)) {
                isCvvValid = true;


            } else {
                isCvvValid = false;
            }
        }

        return isCvvValid;

    }

}