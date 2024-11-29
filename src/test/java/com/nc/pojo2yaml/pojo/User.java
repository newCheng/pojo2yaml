package com.nc.pojo2yaml.pojo;


import java.util.List;

/**
 * 用户
 * @Description
 * @Author czx
 * @Date 2024/7/11 17:26
 */
public class User extends UserBase{

//    /**
//     * 用户名
//     */
//    private String userName;
//
//    /**
//     * 年龄
//     */
//    private Integer age;
//
//    /**
//     * 手机号
//     */
//    private MobilePhone phone;
//
//    /**
//     * 宠物
//     */
//    private Pet pet;

//    /**
//     * 地址
//     */
//    private List<String> addrList;


    /**
     * 联系方式列表
     */
//    private List<ArrayList<Integer>> phoneList;
//    private List<ArrayList<MobilePhone>> phoneList;
    private List<Integer> phoneList;

//    /**
//     * 联系方式map
//     */
//    private List<Map<String,MobilePhone>> phoneMap;

//    @Data
//    @Accessors(chain = true)
//    public class Pet{
//
//        /**
//         * 宠物类型
//         */
//        private String type;
//
//        /**
//         * 宠物名称
//         */
//        private String name;
//
//    }
//
//    public void init(){
//        this.userName = "czx #用户名";
//        this.age = 24;
//        this.phone = new MobilePhone().setArea("86").setNumber("1234567890");
//        this.pet = new Pet().setType("dog").setName("wangcai");
//        this.addr = Arrays.asList("beijing", "shanghai");
//    }

//    public ArrayList<MobilePhone> getPhoneList() {
//        return phoneList;
//    }
//
//    public void setPhoneList(ArrayList<MobilePhone> phoneList) {
//        this.phoneList = phoneList;
//    }

}
