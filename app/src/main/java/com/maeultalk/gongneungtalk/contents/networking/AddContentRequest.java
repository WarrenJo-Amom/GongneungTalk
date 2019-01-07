package com.maeultalk.gongneungtalk.contents.networking;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddContentRequest extends StringRequest {

    final static private String URL = "http://gongneungtalk.cafe24.com/version_code_1/add_content.php";
    private Map<String, String> parameters;

    public AddContentRequest(String spot, String spot_code, String content, String image, String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("spot", spot);
        parameters.put("spot_code", spot_code);
        parameters.put("content", content);
        parameters.put("image", image);
        parameters.put("email", email);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
