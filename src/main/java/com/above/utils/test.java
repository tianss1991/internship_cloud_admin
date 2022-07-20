package com.above.utils;

import com.above.dto.UserDto;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class test {


    public static void main(String[] args) {
//        UserDto userDto = new UserDto();
//        userDto.setId(100);
//        Optional<UserDto> userDto1 = Optional.of(userDto);
//
////        UserDto userDto3 = userDto1.map(user -> user.get(20));
//
//
////        System.out.println("present----->"+ JSON.toJSONString(userDto3));
//
//        try {
//            UserDto userDto2 = userDto1.get();
//        }catch (NoSuchElementException e){
//            System.out.println("对象为空");
//        }

        List<Integer> list1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list1.add(i);
        }
        List<Integer> list2 = new ArrayList<>();
        for (int i = 7; i < 17; i++) {
            list2.add(i);
        }

        int size1 = list1.size();
        int size2 = list2.size();

        long start = System.currentTimeMillis();
        for (int i = 0; i <size1 ; i++) {
            Integer integer = list1.get(i);
            for (int j = 0; j < size2; j++) {
                Integer integer2 = list1.get(j );
                if (integer.equals(integer2)){
                    list1.remove(i);
                    list2.remove(j);
                    size1 -=1;
                    size2 -=1;
                }
            }
        }
        long endFor = System.currentTimeMillis();
        System.out.println("for--"+JSON.toJSONString(list1)+"||"+JSON.toJSONString(list2));
        System.out.println("for用时"+(endFor-start));

        HashSet<Object> objects = new HashSet<>(list1);
        objects.addAll(list2);
        ArrayList<Object> objects1 = new ArrayList<>(objects);
        long endset = System.currentTimeMillis();
        System.out.println("ser---"+JSON.toJSONString(objects1));
        System.out.println("ser用时"+(endset-endFor));

    }
}
