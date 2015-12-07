package test.rit.harsh.mobilevoip;

import java.io.Serializable;

public class Info implements Serializable {
	private static final long serialVersionUID = 1L;
    public  String message=null;
       	Info(String message) {
		this.message=message;
	}
}
