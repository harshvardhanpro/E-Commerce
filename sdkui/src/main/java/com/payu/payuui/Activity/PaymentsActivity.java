package com.payu.payuui.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import com.payu.custombrowser.Bank;
import com.payu.custombrowser.CustomBrowser;
import com.payu.custombrowser.PayUCustomBrowserCallback;
import com.payu.custombrowser.PayUSurePayWebViewClient;
import com.payu.custombrowser.PayUWebChromeClient;
import com.payu.custombrowser.PayUWebViewClient;
import com.payu.custombrowser.bean.CustomBrowserConfig;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Payu.PayuConstants;
import com.payu.magicretry.MagicRetryFragment;
import com.payu.payuui.R;
import java.util.HashMap;
import java.util.Map;

public class PaymentsActivity extends FragmentActivity {
    private Bundle bundle;
    private String url;
    private PayuConfig payuConfig;
    private String UTF = "UTF-8";
    private boolean viewPortWide = false;
    private String merchantHash;
    private String txnId = null;
    private String merchantKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            bundle = getIntent().getExtras();

            if (bundle != null)
                payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);

            if (payuConfig != null) {
                url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV ? PayuConstants.PRODUCTION_PAYMENT_URL : PayuConstants.TEST_PAYMENT_URL;

                String[] list=null;
                if(payuConfig.getData()!=null)
                list = payuConfig.getData().split("&");

                if(list != null) {
                    for (String item : list) {
                        String[] items = item.split("=");
                        if (items.length >= 2) {
                            String id = items[0];
                            switch (id) {
                                case "txnid":
                                    txnId = items[1];
                                    break;
                                case "key":
                                    merchantKey = items[1];
                                    break;
                                case "pg":
                                    if (items[1].contentEquals("NB")) {
                                        viewPortWide = true;
                                    }
                                    break;

                            }
                        }
                    }
                }

                //set callback to track important events
                PayUCustomBrowserCallback payUCustomBrowserCallback = new PayUCustomBrowserCallback() {

                    /**
                     * This method will be called after a failed transaction.
                     *
                     * @param payuResponse     response sent by PayU in App
                     * @param merchantResponse response received from Furl
                     */
                    @Override
                    public void onPaymentFailure(String payuResponse, String merchantResponse) {
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.cb_result), merchantResponse);
                        intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
                        if (null != merchantHash) {
                            intent.putExtra(PayuConstants.MERCHANT_HASH, merchantHash);
                        }
                        setResult(Activity.RESULT_CANCELED, intent);
                        finish();
                    }

                    @Override
                    public void onPaymentTerminate() {
                        finish();
                    }

                    /**
                     * This method will be called after a successful transaction.
                     *
                     * @param payuResponse     response sent by PayU in App
                     * @param merchantResponse response received from Furl
                     */
                    @Override
                    public void onPaymentSuccess(String payuResponse, String merchantResponse) {
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.cb_result), merchantResponse);
                        intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
                        if (null != merchantHash) {
                            intent.putExtra(PayuConstants.MERCHANT_HASH, merchantHash);
                        }
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCBErrorReceived(int code, String errormsg) {
                    }

                    @Override
                    public void setCBProperties(WebView webview, Bank payUCustomBrowser) {
                        webview.setWebChromeClient(new PayUWebChromeClient(payUCustomBrowser));

                        // The following setting is optional, set WV client only when using your custom WVclient
                        // Also, custom WV client should inherit PayUSurePayWebViewClient in case of SurePay enabled,
                        // Otherwise PayUWebViewClient.
                        // webview.setWebViewClient(new PayUWebViewClient(payUCustomBrowser, merchantKey));
                    }

                    @Override
                    public void onBackApprove() {
                        PaymentsActivity.this.finish();
                    }

                    @Override
                    public void onBackDismiss() {
                        super.onBackDismiss();
                    }

                    /**
                     * This callback method will be invoked when setDisableBackButtonDialog is set to true.
                     *
                     * @param alertDialogBuilder a reference of AlertDialog.Builder to customize the dialog
                     */
                    @Override
                    public void onBackButton(AlertDialog.Builder alertDialogBuilder) {
                        super.onBackButton(alertDialogBuilder);
                    }

                    //TODO Below code is used only when magicRetry is set to true in customBrowserConfig
/*                    @Override
                    public void initializeMagicRetry(Bank payUCustomBrowser, WebView webview, MagicRetryFragment magicRetryFragment) {
                        webview.setWebViewClient(new PayUWebViewClient(payUCustomBrowser, magicRetryFragment, merchantKey));
                        Map<String, String> urlList = new HashMap<String, String>();
                        if(payuConfig!=null)
                            urlList.put(url, payuConfig.getData());
                        payUCustomBrowser.setMagicRetry(urlList);
                    }*/
                };

                //Sets the configuration of custom browser
                CustomBrowserConfig customBrowserConfig = new CustomBrowserConfig(merchantKey, txnId);
                customBrowserConfig.setViewPortWideEnable(viewPortWide);

                //TODO don't forgot to set AutoApprove and AutoSelectOTP to true for One Tap payments
                customBrowserConfig.setAutoApprove(false);  // set true to automatically approve the OTP
                customBrowserConfig.setAutoSelectOTP(false); // set true to automatically select the OTP flow

                //Set below flag to true to disable the default alert dialog of Custom Browser and use your custom dialog
                customBrowserConfig.setDisableBackButtonDialog(false);

                //Below flag is used for One Click Payments. It should always be set to CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER
                customBrowserConfig.setStoreOneClickHash(CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER);

                //Set it to true to enable run time permission dialog to appear for all Android 6.0 and above devices
                customBrowserConfig.setMerchantSMSPermission(false);

                //Set it to true to enable Magic retry (If MR is enabled SurePay should be disabled and vice-versa)
                customBrowserConfig.setmagicRetry(false);

                /**
                 * Maximum number of times the SurePay dialog box will prompt the user to retry a transaction in case of network failures
                 * Setting the sure pay count to 0, diables the sure pay dialog
                 */
                customBrowserConfig.setEnableSurePay(3);

                /**
                 * set Merchant activity(Absolute path of activity)
                 * By the time CB detects good network, if CBWebview is destroyed, we resume the transaction by passing payment post data to,
                 * this, merchant checkout activity.
                 * */
                customBrowserConfig.setMerchantCheckoutActivityPath("com.payu.testapp.MerchantCheckoutActivity");

                //Set the first url to open in WebView
                customBrowserConfig.setPostURL(url);

                if (payuConfig!=null)
                customBrowserConfig.setPayuPostData(payuConfig.getData());

                new CustomBrowser().addCustomBrowser(PaymentsActivity.this, customBrowserConfig, payUCustomBrowserCallback);
            }
        }
    }
}
