package org.anonymization.examples;

public class StrShift {
    public static void main(String[] a){
        String val="hello";
        for (int i=val.length()-1;i>=0;i--){
            StringBuilder sb=new StringBuilder(val);
            sb.setCharAt(i,'*');
            val=sb.toString();
            sb=null;
        }
    }
}
