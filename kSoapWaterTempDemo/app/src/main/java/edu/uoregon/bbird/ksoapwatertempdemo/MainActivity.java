package edu.uoregon.bbird.ksoapwatertempdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Proxy;

// Written by Brian Bird, 7/12/16
// Demonstrates using kSOAP2-Android to make a call to a web service
// Uses a NOAA web service for ocean water temperature data
// Informaiton page for this and other NOAA web services:
// http://opendap.co-ops.nos.noaa.gov/axis/

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SoapTask soapTask = new SoapTask();
        soapTask.execute();
    }


    /* nested class for doing a web service call on a background thread
     Generic types:
     1. Params, the type of the parameters sent to the task upon execution.
     2. Progress, the type of the progress units published during the background computation.
     3. Result, the type of the result of the background computation.
    */
    private class SoapTask  extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

        /* Query a web service using kSoap */
            // 1. Create a SOAP request object, constructor wants namespace and operation
            final String TARGET_NAMESPACE =
                    "http://opendap.co-ops.nos.noaa.gov/axis/webservices/watertemperature/wsdl";
            final String OPERATION_NAME = "getWaterTemperature";
            SoapObject request = new SoapObject(TARGET_NAMESPACE, OPERATION_NAME);
            request.addProperty("stationId", "9432780");         // type="xsd:string"
            request.addProperty("beginDate", "20160712 00:00");  // type="xsd:string"
            request.addProperty("endDate", "20160712 23:59");    // type="xsd:string"
            // the following parameter is optional, celsius is the default:
            // request.addProperty("unit", "Celsius");              // nillable="true" type="xsd:string"
            request.addProperty("timeZone", 1);                  // type="xsd:int"

            // 2. create SOAP envelope and add the request to it
            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            // envelope.implicitTypes = true;  // we only need this if we use the wrong type in addProperty
            envelope.setOutputSoapObject(request);

            // 3. setup the trasport object
            final String ENDPOINT =
                    "http://opendap.co-ops.nos.noaa.gov/axis/services/WaterTemperature";
            HttpTransportSE transport = new HttpTransportSE(Proxy.NO_PROXY, ENDPOINT, 10000);
            transport.debug = true;
            final String SOAP_ACTION = ENDPOINT + "/" + OPERATION_NAME;
            try

            {
                transport.call(SOAP_ACTION, envelope);
            } catch (
                    IOException e
                    )

            {
                e.printStackTrace();
            } catch (
                    XmlPullParserException e
                    )

            {
                e.printStackTrace();
            }

            // 4. Get response
            String xmlResponse = transport.responseDump;
            return xmlResponse;
        }

        @Override
        protected void onPostExecute(String xml) {
            super.onPostExecute(xml);
        }
    }
}


