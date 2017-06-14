package com.payu.payuui.Fragment;


import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Interfaces.GetOfferStatusApiListener;
import com.payu.india.Model.CardStatus;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.Tasks.GetOfferStatusTask;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.payu.payuui.R;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;
import com.payu.payuui.Widget.MonthYearPickerDialog;

import java.util.Calendar;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreditDebitFragment extends Fragment implements GetOfferStatusApiListener {

    private PayuHashes mPayuHashes;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private PayuUtils payuUtils;
    private PostData postData;
    private MerchantWebService merchantWebService;

    private boolean isCvvValid = false;
    private boolean isExpiryMonthValid = false;
    private boolean isExpiryYearValid = false;
    private boolean isCardNumberValid = false;

    private String cvv;
    private Bundle fragmentBundle;
    private Bundle activityBundle;
    private String issuer;
    private HashMap<String, CardStatus> valueAddedHashMap;
    private EditText nameOnCardEditText;
    private EditText cardNumberEditText;
    private EditText cardCvvEditText;
    private EditText cardExpiryMonthEditText;
    private EditText cardExpiryYearEditText;
    private EditText cardNameEditText;
    private CheckBox saveCardCheckBox;
    private CheckBox enableOneClickPaymentCheckBox;
    private ImageView cardImage;
    private ImageView cvvImage;
    private DatePickerDialog.OnDateSetListener datePickerListener;
    private LinearLayout mLinearLayout;
    private TextView amountText;
    private TextView issuingBankDown;
    private ViewPager viewpager;
    private int fragmentPosition;
    private View view;

    public CreditDebitFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentBundle = getArguments();
        valueAddedHashMap = (HashMap<String, CardStatus>) fragmentBundle.getSerializable(SdkUIConstants.VALUE_ADDED);
        fragmentPosition = fragmentBundle.getInt(SdkUIConstants.POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_credit_debit, container, false);

        viewpager = (ViewPager) getActivity().findViewById(R.id.pager);


        nameOnCardEditText = (EditText) view.findViewById(R.id.edit_text_name_on_card);
        cardNumberEditText = (EditText) view.findViewById(R.id.edit_text_card_number);
        cardCvvEditText = (EditText) view.findViewById(R.id.edit_text_card_cvv);
        cardExpiryMonthEditText = (EditText) view.findViewById(R.id.edit_text_expiry_month);
        cardExpiryYearEditText = (EditText) view.findViewById(R.id.edit_text_expiry_year);
        cardNameEditText = (EditText) view.findViewById(R.id.edit_text_card_label);
        saveCardCheckBox = (CheckBox) view.findViewById(R.id.check_box_save_card);
        enableOneClickPaymentCheckBox = (CheckBox) view.findViewById(R.id.check_box_enable_oneclick_payment);
        cardImage = (ImageView) view.findViewById(R.id.image_card_type);
        cvvImage = (ImageView) view.findViewById(R.id.image_cvv);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.layout_expiry_date);
        issuingBankDown = (TextView) view.findViewById(R.id.text_view_issuing_bank_down_error);

        amountText = (TextView) getActivity().findViewById(R.id.textview_amount);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            cardExpiryMonthEditText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    MonthYearPickerDialog newFragment = new MonthYearPickerDialog();
                    newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
                    newFragment.setListener(datePickerListener);

                }
            });

            cardExpiryYearEditText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    MonthYearPickerDialog newFragment = new MonthYearPickerDialog();
                    newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
                    newFragment.setListener(datePickerListener);

                }
            });


            datePickerListener
                    = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedDay,
                                      int selectedMonth, int selectedYear) {
                    cardExpiryYearEditText.setText("" + selectedYear);
                    cardExpiryMonthEditText.setText("" + selectedMonth);


                    if(!cardExpiryMonthEditText.getText().toString().equals("") && !cardExpiryYearEditText.getText().toString().equals("")) {
                        isExpiryYearValid = true;
                        isExpiryMonthValid = true;
                    }
                    if(selectedYear == Calendar.YEAR && selectedMonth < Calendar.MONTH){
                        isExpiryMonthValid = false;
                    }

                    uiValidation();
                }
            };

        }
        else{

            cardExpiryYearEditText.setFocusableInTouchMode(true);
            cardExpiryMonthEditText.setFocusableInTouchMode(true);

            cardExpiryYearEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    charSequence.toString();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(editable.length() == 4 && Integer.parseInt(editable.toString()) >= Calendar.YEAR){

                        isExpiryYearValid = true;

                    }
                    else
                        isExpiryYearValid = false;

                }

            });

        }
        cardNameEditText.setVisibility(View.GONE);
        enableOneClickPaymentCheckBox.setVisibility(View.GONE);

        saveCardCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (compoundButton.isChecked()) {
                    if(null != mPaymentParams.getUserCredentials()) {
                        enableOneClickPaymentCheckBox.setVisibility(View.VISIBLE);
                    }
                    cardNameEditText.setVisibility(View.VISIBLE);
                } else {
                    cardNameEditText.setVisibility(View.GONE);
                    enableOneClickPaymentCheckBox.setVisibility(View.GONE);
                    enableOneClickPaymentCheckBox.setChecked(false);
                }

            }
        });



        activityBundle = ((PayUBaseActivity) getActivity()).bundle;
        mPaymentParams = activityBundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
        mPayuHashes = activityBundle.getParcelable(PayuConstants.PAYU_HASHES);





        payuConfig = activityBundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();


        if (null == mPaymentParams.getUserCredentials() || mPaymentParams.getUserCredentials().equals("") || !mPaymentParams.getUserCredentials().contains(":")) {
            saveCardCheckBox.setVisibility(View.GONE);
        }
        else {
            saveCardCheckBox.setVisibility(View.VISIBLE);
        }



        payuUtils = new PayuUtils();

        cardNumberEditText.addTextChangedListener(new TextWatcher() {

            int image;
            int cardLength;
            int setSpacesIndex = 4;
            private String ccNumber = "";
            int afterTextPosition;
            private static final char space = ' ';
            int currentPosition;
            int len = 0;


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 6) { // to confirm rupay card we need min 6 digit.
                    if (null == issuer){
                        issuer = payuUtils.getIssuer(charSequence.toString().replace(" ",""));
                    }
                    if (issuer != null && issuer.length() > 1 ) {
                        image = getIssuerImage(issuer);
                        cardImage.setImageResource(image);

                        if(issuer == "AMEX")
                            cardCvvEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
                        else
                            cardCvvEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                        if(issuer == "SMAE" || issuer == "MAES") {
                            cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                            cardLength = 23;
                        }else if(issuer == "AMEX"){
                            cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                            cardLength = 18;
                        }
                        else if(issuer == "DINR"){
                            cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
                            cardLength = 17;
                        }
                        else {
                            cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                            cardLength = 20;
                        }
                    }
                } else {
                    issuer = null;
                    ((LinearLayout)view.findViewById(R.id.layout_expiry_cvv)).setVisibility(View.VISIBLE);
                    cardImage.setImageResource(R.drawable.icon_card);
                    cardCvvEditText.getText().clear();

                }

                if(charSequence.length() == 7){

                    //lets do a null check on valueAddedHashMap
                    if(null != valueAddedHashMap) {
                        if (valueAddedHashMap.get(charSequence.toString().replace(" ", "")) != null) {
                            int statusCode = valueAddedHashMap.get(charSequence.toString().replace(" ", "")).getStatusCode();

                            if (statusCode == 0) {
                                issuingBankDown.setVisibility(View.VISIBLE);
                                issuingBankDown.setText(valueAddedHashMap.get(charSequence.toString().replace(" ", "")).getBankName() + " is temporarily down");
                            } else {
                                issuingBankDown.setVisibility(View.GONE);
                            }

                        } else {
                            issuingBankDown.setVisibility(View.GONE);
                        }
                    }
                }else if(charSequence.length() < 7 ){
                    issuingBankDown.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().replace(" ","").length() > cardLength - (cardLength/5) && s.toString().replace(" ","").length() >= 6 ){
                    s.delete(cardLength - (cardLength/5) , s.length());
                }

                // Remove all spacing char
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (' ' == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + ' ');
                    }
                    pos += 5;
                }

                if(cardNumberEditText.getSelectionStart() > 0 && s.charAt(cardNumberEditText.getSelectionStart()-1) == ' '){
                    cardNumberEditText.setSelection(cardNumberEditText.getSelectionStart()-1);
                }

                if(s.length() >= cardLength-1){
                    cardValidation();
                }else{
                    isCardNumberValid = false;
                    if(viewpager.getCurrentItem() == fragmentPosition)
                        getActivity().findViewById(R.id.button_pay_now).setEnabled(false);
                }


            }

        });

        cardNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    cardValidation();
                }
            }
        });

        cardCvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cvv = charSequence.toString();
                if (payuUtils.validateCvv(cardNumberEditText.getText().toString().replace(" ",""), cvv)) {
                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    cvvImage.setAlpha((float)1);
                    isCvvValid = true;
                    uiValidation();
                } else{
                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    cvvImage.setAlpha((float)0.5);
                    isCvvValid = false;
                    uiValidation();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });



        return view;
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
                case PayuConstants.MAES:
                    return R.drawable.mas_icon;
                case PayuConstants.MAST:
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
                case PayuConstants.MAES:
                    return R.drawable.mas_icon;
                case PayuConstants.MAST:
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



    public void onClickFunction() {

    }

    public void cardValidation(){

        if (!(payuUtils.validateCardNumber(cardNumberEditText.getText().toString().replace(" ", ""))) && cardNumberEditText.length() > 0 ) {
            cardImage.setImageResource(R.drawable.error_icon);
            isCardNumberValid = false;
            amountText.setText(SdkUIConstants.AMOUNT + ": " + mPaymentParams.getAmount());
//            uiValidation();
        }
        else if(payuUtils.validateCardNumber(cardNumberEditText.getText().toString().replace(" ", "")) && cardNumberEditText.length() > 0){
            isCardNumberValid = true;
            if(mPaymentParams.getOfferKey() != null && null != mPaymentParams.getUserCredentials())
            getOfferStatus();
//            uiValidation();
        }else{
            isCardNumberValid = false;

        }
        uiValidation();
    }

    private void getOfferStatus() {

        merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.CHECK_OFFER_STATUS);
        merchantWebService.setHash(mPayuHashes.getCheckOfferStatusHash());
        merchantWebService.setVar1(mPaymentParams.getOfferKey());
        merchantWebService.setVar2(mPaymentParams.getAmount());
        merchantWebService.setVar3("CC");
        merchantWebService.setVar4("CC");
        merchantWebService.setVar5(cardNumberEditText.getText().toString().replace(" ",""));
        merchantWebService.setVar6(cardNameEditText.getText().toString());
        merchantWebService.setVar7("abc");
        merchantWebService.setVar8("abc@gmail.com");

        postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

        if(postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());

            GetOfferStatusTask getOfferStatusTask = new GetOfferStatusTask(CreditDebitFragment.this);
            getOfferStatusTask.execute(payuConfig);

            // lets cancel the dialog.
        }else{
            Toast.makeText(getActivity(), postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    public void uiValidation(){

        if(issuer == "SMAE"){

            isCvvValid = true;
            isExpiryMonthValid = true;
            isExpiryYearValid = true;

        }

        if(isCardNumberValid && isCvvValid && isExpiryYearValid && isExpiryMonthValid && fragmentPosition == viewpager.getCurrentItem()){
            getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
        }
        else {
            if(viewpager.getCurrentItem() == fragmentPosition)
            getActivity().findViewById(R.id.button_pay_now).setEnabled(false);
        }

    }

    @Override
    public void onGetOfferStatusApiResponse(PayuResponse payuResponse) {

        if(getActivity()!= null && payuResponse.getPayuOffer().getDiscount() != null ) {
            Toast.makeText(getActivity(), "Response status: " + payuResponse.getResponseStatus().getResult() + ": Discount = " + payuResponse.getPayuOffer().getDiscount(), Toast.LENGTH_LONG).show();


            Double amount = Double.parseDouble(mPaymentParams.getAmount()) - Double.parseDouble(payuResponse.getPayuOffer().getDiscount());
            String discountedAmount = "" + amount;
            amountText.setText(SdkUIConstants.AMOUNT + ": " + discountedAmount);

        }

        else
            amountText.setText(SdkUIConstants.AMOUNT + ": " + mPaymentParams.getAmount());

    }

    public void checkData(){
        if(!cardExpiryYearEditText.getText().toString().equals("") && !cardExpiryMonthEditText.getText().toString().equals("")){
            isExpiryYearValid = true;
            isExpiryMonthValid = true;
        }
        if(!cardCvvEditText.getText().toString().equals("") && !cardNumberEditText.getText().toString().equals("") && payuUtils.validateCvv(cardNumberEditText.getText().toString().replace(" ",""), cvv)){
            isCvvValid = true;
        }
        cardValidation();
    }

}
