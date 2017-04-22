package com.mishwar.utils;

import android.app.Activity;
import android.content.Intent;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

public class PaymentClass {

	Activity context;
	PayPalConfiguration config;
	//private static String paypalid="AcNOqryEgkPqPPNRrILG7TiYxn1EaUWFFZeL3Fxk1_uCMW6Q1TssbKIR8uSvp7lh1BpRGuXAfc0ul_vX";
	public static String CONFIG_CLIENT_ID="AXFdRBn-6FOHRVaf68U2zgPEpWG52_qIZStzjjo6NG2UZOTeEBC2nZmUmFrrLWvGog3Tls5o-1vJmauo";


	public PaymentClass(Activity context)
	{
		this.context = context;

		configurationPaypal();

		startPaypalService();
	}

	private void configurationPaypal()
	{
		config = new PayPalConfiguration()

				// Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
				// or live (ENVIRONMENT_PRODUCTION)
				.environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)

				.clientId(CONFIG_CLIENT_ID);
	}

	private void startPaypalService()
	{
		Intent intent = new Intent(context, PayPalService.class);

		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

		context.startService(intent);
	}

	public void starProcessToPay(String ammount,String currency, int requestCode,String description)
	{
		PayPalPayment payment = new PayPalPayment(new BigDecimal(ammount), currency, description,
				PayPalPayment.PAYMENT_INTENT_SALE);

		Intent intent = new Intent(context, PaymentActivity.class);

		// send the same configuration for restart resiliency
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

		context.startActivityForResult(intent, requestCode);
	}

}