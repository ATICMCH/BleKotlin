package com.mch.blekot

import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

class WeLock(
    private var mRndNumber: String,
    private var mDevicePower: String,
    private var mAction: String,
    private val ble: Ble
) {

    private val urlWeLock = "https://api.we-lock.com"
    private val PATH_CARD = "/API/Device/DeviceCardCommand"
    private val PATH_CODE = "/API/Device/DeviceSetTemporaryPassword"
    private val PATH_CODE_DELETE = "/API/Device/DeviceDeleteTemporaryPassword"
    private val PATH_OPEN = "/API/Device/DeviceUnLockCommand"
    private val PATH_TOKEN = "/API/Auth/Token"

    private var client: OkHttpClient = OkHttpClient()
    private var mToken: String? = null
    private var mCode: String? = null
    private var mType: String? = null
    private var mQr: String? = null


    fun getHex() {
        val startDate: Int = ((System.currentTimeMillis() / 1000) - 28800).toInt()

        val endDate: Int = startDate + 86400


        // deviceNumber: "21471618"
        // deviceBleName: "WeLockAWPOR"
        val openLockJson = """{
            appID: "WELOCK2202161033", 
            deviceNumber: "21471175", 
            deviceBleName: "WeLockE31J8", 
            devicePower: "$mDevicePower", 
            deviceRandomFactor: "$mRndNumber"}
            """.trimIndent()

        val newCodeJson = """{
            appID: "WELOCK2202161033", 
            deviceNumber: "21471175", 
            deviceBleName: "WeLockE31J8",
            deviceRandomFactor: "$mRndNumber", 
            password: $mCode, 
            index: 20, 
            user: 15, 
            times: 5, 
            startTimestamp: $startDate, 
            endTimestamp: $endDate}
        """.trimIndent()

        //times: 65000,

        val newCodeJson1 = """{
            appID: "WELOCK2202161033", 
            deviceNumber: "21471175", 
            deviceBleName: "WeLockE31J8",
            deviceRandomFactor: "$mRndNumber",
            cardID: "M821110010778", 
            cardQr: "https://download.we-lock.com/app/dl/2/a666a500a8267e82", 
            type: 1}
        """.trimIndent()





        when (mAction) {
            "openLock" -> postWithToken(
                PATH_OPEN,
                openLockJson,
                actionCallback
            )
            "newCode" -> {
                Log.i("Json", newCodeJson1)

                postWithToken(
                    PATH_CARD,
                    newCodeJson1,
                    actionCallback
                )
                /*Log.i("Json", newCodeJson)

                postWithToken(
                    PATH_CODE,
                    newCodeJson,
                    actionCallback
                )*/
            }

        }
    }

    fun getToken(qr: String? = null, code: String? = null, type: String? = null) {

        when (mAction) {
            "newCode" -> {
                mCode = code!!
            }

            "setCard" -> {
                mQr = qr!!
                mType = type!!
            }
        }

        post(
            "/API/Auth/Token", newJson(
                "WELOCK2202161033",
                "349910dfcdfac75df0fd1cf2cbb02adb"
            ), tokenCallback
        )
    }

    private var tokenCallback: Callback = object : Callback {

        override fun onFailure(p0: Call, p1: IOException) {
            TODO("Not yet implemented")
        }

        override fun onResponse(p0: Call, response: Response) {
            mToken = response.body()?.string()?.split("\"")?.get(9)
            Log.i("Token", "onResponse: $mToken")
            getHex()
        }
    }


    private var actionCallback: Callback = object : Callback {

        override fun onFailure(p0: Call, p1: IOException) {
            Log.i("Action Callback", "FAIL")
        }

        override fun onResponse(p0: Call, response: Response) {
            var str_response = response.body()?.string()?.trim();
            //val res = response.body()?.string()?.split("\"")?.get(3)
            //Log.i("Action", "onResponse: $res")
            //Log.i("mAction", "${mAction}");
            //val stringResponse = response.body()?.string()

            //val res1 = response.body()?.string()?.split("\"")
            //Log.i("res1 size", "onResponse: ${res1?.size}")

            //var str_response = response.body()?.string()
            if (str_response != null) {
                Log.i("str_response", str_response)
                //var dataResponse: JSONArray = JSONArray(str_response).getJSONArray(0)
                //var json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
                //val json_contact: JSONObject = JSONObject(str_response)
                var json_objectdetail:JSONObject= JSONObject(str_response)
                var codeWL = json_objectdetail.getString("code")
                //var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
                //var i:Int = 0
                //var size:Int = jsonarray_info.length()
                Log.i("json-code", codeWL);
                if ( codeWL.compareTo("0") === 0 ) {
                    var dataWL = json_objectdetail.getString("data")
                    Log.i("json-data", dataWL)
                    ble.state = true
                    //dataTmp = "554261503155555571354141414135";
                    ble.startBle(code = dataWL, action = mAction)
                    //ble.startBle(code = dataTmp, action = mAction)
                }

            }
            /*if (stringResponse != null) {
                Log.i("json11", stringResponse)
            }*/

            //if (res != null) {
                //ble.startBle(code = res)
            //}
        }
    }


    private fun newJson(appID: String, secret: String): String {
        return """{appID: "$appID", secret: "$secret"}"""
    }

    @Throws(IOException::class)
    fun post(path: String, json: String?, callback: Callback?) {
        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"), json
        )
        val request: Request = Request.Builder()
            .url(urlWeLock + path)
            .post(body)
            .build()
        val call: Call = client.newCall(request)
        if (callback != null) {
            call.enqueue(callback)
        }
    }

    @Throws(IOException::class)
    fun postWithToken(path: String, json: String?, callback: Callback?) {
        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), json?.toByteArray()!!
        )
        val request: Request = Request.Builder()
            .url(urlWeLock + path)
            .addHeader("Authorization", " Bearer $mToken")
            .post(body)
            .build()
        val call: Call = client.newCall(request)
        if (callback != null) {
            call.enqueue(callback)
        }
    }

}