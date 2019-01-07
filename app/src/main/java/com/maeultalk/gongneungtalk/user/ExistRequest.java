package com.maeultalk.gongneungtalk.user;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ExistRequest extends StringRequest {

    final static private String URL = "http://gongneungtalk.cafe24.com/version_code_1/exist_user.php";
    private Map<String, String> parameters;

    public ExistRequest(String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("email", email);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
