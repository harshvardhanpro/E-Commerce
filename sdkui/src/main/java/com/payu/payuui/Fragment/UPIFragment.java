package com.payu.payuui.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class UPIFragment extends Fragment {

    private OnCLickListener onCLickListener;
    private PopupWindow pwHelperUPI;
    private TextView tvUPIInfo;
    private SpannableString vpaSpannableMore ;
    private SpannableString vpaSpannableLess;
    private EditText etVirtualAddress;
    public UPIFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upi, container, false);
        setUpUI(view);
        return view;

    }

    /**
     * Initialize UI Component of fragment
     * @param view fragment layout
     */
    private void setUpUI(View view){
        tvUPIInfo = (TextView) view.findViewById(R.id.tv_upi_info);
        TextView  tvLearnUpi = (TextView) view.findViewById(R.id.tv_learn_upi);
        etVirtualAddress = (EditText) view.findViewById(R.id.et_virtual_address);

        OnTextChangeListener onTextChangeListener = new OnTextChangeListener();
        onCLickListener = new OnCLickListener();
        etVirtualAddress.addTextChangedListener(onTextChangeListener);
        etVirtualAddress.setOnClickListener(onCLickListener);
        setSpannableTextView();


        tvLearnUpi.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica_neue_bold.ttf");
        tvLearnUpi.setTypeface(typeface);
        tvLearnUpi.setOnClickListener(onCLickListener);
    }

    /**
     * Set the Info about UPI in TextView
     * Handle the 'show less' and 'show more' info option of UPI
     */
    private void setSpannableTextView(){
        String vpaMoreText = getActivity().getString(R.string.vpa_text_more);
        String vpaLessText = getActivity().getString(R.string.vpa_text_less);

        String showLess = getActivity().getString(R.string.show_less);
        String showMore = getActivity().getString(R.string.show_more);


        vpaSpannableMore = new SpannableString(vpaMoreText+" "+showLess);
        vpaSpannableLess = new SpannableString(vpaLessText+" "+showMore);
        ClickableSpan clickableSpanLess = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do another thing
                tvUPIInfo.setText(vpaSpannableLess);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getColor(getActivity().getApplicationContext(), R.color.cb_vpa_text_highlighter));
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan clickableSpanMore = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do another thing
                tvUPIInfo.setText(vpaSpannableMore);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getColor(getActivity().getApplicationContext(), R.color.cb_vpa_text_highlighter));
                ds.setUnderlineText(true);
            }
        };

        vpaSpannableLess.setSpan(clickableSpanMore, vpaLessText.length()+1, (vpaLessText+showMore).length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        vpaSpannableMore.setSpan(clickableSpanLess, vpaMoreText.length()+1, (vpaMoreText+showLess).length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUPIInfo.setText(vpaSpannableLess);
        tvUPIInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Called when activity staretd for result is finished
     * @param requestCode return code from activity
     * @param resultCode result from activity
     * @param data intent from stated activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            getActivity().setResult(resultCode, data);
            getActivity().finish();
        }
    }

    /**
     * Display the helper screen for UPI
     */
    private void showHelperScreen(){
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View helperView = inflater.inflate(R.layout.upi_helper, null, false);
        pwHelperUPI = new PopupWindow(helperView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        (helperView.findViewById(R.id.close_button)).setOnClickListener(onCLickListener);
        pwHelperUPI.showAtLocation(getActivity().findViewById(R.id.tv_learn_upi), Gravity.CENTER, 0, 0);
    }

    /**
     * Return the color res ID
     * @param context application context
     * @param id res ID
     * @return color res id
     */
    private int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    private class OnTextChangeListener implements TextWatcher {

        private int intialTextSize;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            intialTextSize = count;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!(count== PayuConstants.MAX_VPA_SIZE && intialTextSize > PayuConstants.MAX_VPA_SIZE)){
                etVirtualAddress.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s.length()> PayuConstants.MAX_VPA_SIZE){
                etVirtualAddress.setError(getActivity().getText(R.string.error_invalid_vpa));
                etVirtualAddress.setText(s.subSequence(0, PayuConstants.MAX_VPA_SIZE));
                etVirtualAddress.setSelection(PayuConstants.MAX_VPA_SIZE);
            }else{


                for(int i = s.length(); i > 0; i--) {
                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }

        }
    }

    private class OnCLickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId()== R.id.tv_learn_upi){
                showHelperScreen();
            }else if(v.getId()== R.id.close_button){
                pwHelperUPI.dismiss();
            }else if(v.getId()== R.id.et_virtual_address){
                etVirtualAddress.setError(null);
            }
        }
    }
}