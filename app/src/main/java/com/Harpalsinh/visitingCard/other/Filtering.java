package com.Harpalsinh.visitingCard.other;

import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HARPALSINH on 30-03-2017.
 */

public class Filtering
{

    public Filtering() {
    }

    public String filterr(List<Text> t)
    {
        String json="";

        return json;
    }
    public List<String> getList(List<Text> t)
    {
        List<String> re=new ArrayList<String>();
        for (Text te:t) {
            re.add(te.getValue());
        }
        return re;
    }
}
