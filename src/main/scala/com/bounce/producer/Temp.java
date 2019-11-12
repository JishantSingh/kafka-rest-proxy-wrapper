package com.bounce.producer;
import com.bounce.producer.WrappedKafkaAvroProducer$;
import sun.plugin.javascript.navig.Array;

import java.util.ArrayList;
import java.util.List;

public class Temp {

    public static void main(String[] args) {
        WrappedKafkaAvroProducer$ b = WrappedKafkaAvroProducer$.MODULE$;

        b.apply();
        Integer [] a = {1,2,3,4};
//        for(int i = 0 ; i <6; i++) ll.add(i);
        b.send(a);

    }
}
