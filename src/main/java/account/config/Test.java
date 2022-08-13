package account.config;

import account.db.entity.Role;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.logging.Handler;

public class Test {


    public static void main(String[] args) {
//        Payroll payroll= Payroll.builder()
//                .salary(1200L)
//                .build();
//
//        String dollars;
//        String cents;
//        String salary=String.valueOf(payroll.getSalary());
//        if (payroll.getSalary()<100){
//            dollars="0";
//            cents= salary;
//        } else{
//            dollars=salary.substring(0,salary.length()-2);
//            if (payroll.getSalary() % 10==0&&payroll.getSalary()>100)
//                cents="0";
//            else
//                cents=salary.substring(dollars.length());
//        }
//        System.out.println(dollars);
//        System.out.println(cents);

//        DateUtils dateUtils=new DateUtils();
//        System.out.println(dateUtils.validateRequestFormat("13-2022"));


        String header="Basic c2FteWFsaTEzQGFjbWUuY29tOlBhc3MkMTIzNDU2Nw==";

        header=header.split(" ")[1];
        String decoded=new String(Base64.decodeBase64(header)).split(":")[0];

    }
}
