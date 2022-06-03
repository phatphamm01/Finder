package com.summon.finder.model;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UserModel {
    private HashMap<String, String> images;
    private List<String> tags;
    private String phone, uid, name, gender, birthday, matchGender, school, lastOperatingTime, typeLogin, password;
    private float matchDistance;
    private List<Float> matchAge;
    private Boolean active, working, statusMatchDistance, statusMatchAge;
    private LocationModel location;


    public UserModel() {
        initData();
    }

    public UserModel(DataSnapshot dataSnapshot) {
        initData();
        UserModelMapping user = dataSnapshot.getValue(UserModelMapping.class);
        this.uid = user.uid;
        this.phone = user.phone;
        this.name = user.name;
        this.gender = user.gender;
        this.birthday = user.birthday;
        this.matchGender = user.matchGender;
        this.lastOperatingTime = user.lastOperatingTime;
        this.school = user.school;
        this.tags = user.tags == null ? new ArrayList<>() : user.tags;
        this.active = user.active;
        this.working = user.working;
        this.location = user.location;
        this.typeLogin = user.typeLogin;
        this.password = user.password;
        this.matchDistance = user.matchDistance;
        this.matchAge = user.matchAge;
        this.statusMatchDistance = user.statusMatchDistance;
        this.statusMatchAge = user.statusMatchAge;

        handleSetImages(dataSnapshot);
    }

    private static HashMap<String, String> convertArrayListToHashMap(ArrayList<String> arrayList) {
        HashMap<String, String> hashMap = new HashMap<>();

        int i = 1;
        for (String str : arrayList) {
            if (str == null) continue;

            hashMap.put(String.valueOf(i++), str);
        }

        return hashMap;
    }

    private void handleSetImages(DataSnapshot dataSnapshot) {
        Object imagesObject = dataSnapshot.child("images").getValue();

        if (imagesObject instanceof HashMap) {
            this.images = (HashMap<String, String>) imagesObject;
        }

        if (imagesObject instanceof ArrayList) {
            HashMap<String, String> imagesData = convertArrayListToHashMap((ArrayList<String>) imagesObject);
            this.images = imagesData;
        }

        if (imagesObject == null) {
            this.images = new HashMap<String, String>();
        }
    }

    public String handleGetAge() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        String currentData = this.getBirthday();
        String nowDate = dateFormat.format(date);

        return String.valueOf(handleGetYear(nowDate) - handleGetYear(currentData));
    }

    private Integer handleGetYear(String date) {
        return Integer.valueOf(date.substring(date.lastIndexOf("/") + 1));
    }

    public long handleGetDistance(UserModel userModel) {
        if (this.getLocation() == null || userModel.getLocation() == null) {
            return -1;
        }

        double lat1 = this.getLocation().getLat();
        double lon1 = this.getLocation().getLon();
        double lat2 = userModel.getLocation().getLat();
        double lon2 = userModel.getLocation().getLon();


        double p = 0.017453292519943295;    // Math.PI / 180

        double a = 0.5 - Math.cos((lat2 - lat1) * p) / 2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p)) / 2;

        return Math.round(12742 * Math.asin(Math.sqrt(a))); // 2 * R; R = 6371 km
    }

    private void initData() {
        images = new HashMap<String, String>();
        tags = new ArrayList<>();
        matchAge = new ArrayList<>();
        uid = "";
        name = "";
        gender = "";
        birthday = "";
        matchGender = "";
        school = "";
        active = false;
        matchDistance = 2;
        matchAge.add(18f);
        matchAge.add(23f);
        statusMatchAge = false;
        statusMatchDistance = false;
    }

    public String firstImage() {
        return images.values().stream().findFirst().get();
    }


    public boolean equals(UserModel object) {
        String objectString1 = new Gson().toJson(this);
        String objectString2 = new Gson().toJson(object);
        return objectString1.equals(objectString2);
    }

    public float getMatchDistance() {
        return matchDistance;
    }

    public void setMatchDistance(float matchDistance) {
        this.matchDistance = matchDistance;
    }

    public List<Float> getMatchAge() {
        return matchAge;
    }

    public void setMatchAge(List<Float> matchAge) {
        this.matchAge = matchAge;
    }

    public Boolean getStatusMatchDistance() {
        return statusMatchDistance;
    }

    public void setStatusMatchDistance(Boolean statusMatchDistance) {
        this.statusMatchDistance = statusMatchDistance;
    }

    public Boolean getStatusMatchAge() {
        return statusMatchAge == null;
    }

    public void setStatusMatchAge(Boolean statusMatchAge) {
        this.statusMatchAge = statusMatchAge;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getTypeLogin() {
        return typeLogin;
    }

    public void setTypeLogin(String typeLogin) {
        this.typeLogin = typeLogin;
    }

    public String getLastOperatingTime() {
        return lastOperatingTime;
    }

    public void setLastOperatingTime(String lastOperatingTime) {
        this.lastOperatingTime = lastOperatingTime;
    }

    public Boolean getWorking() {
        return working;
    }

    public void setWorking(Boolean working) {
        this.working = working;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        HashMap<String, String> newImages = new HashMap<String, String>();
        for (int i = 0; i < images.size(); i++) {
            newImages.put(String.valueOf(i + 1), images.get(i));
        }
        this.images = newImages;
    }

    public void addImages(HashMap<String, String> images) {
        this.images = images;
    }

    public void handleSetImage(String key, String value) {
        images.put(key, value);
    }

    public void removeImage(String key) {
        images.remove(key);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMatchGender() {
        return matchGender;
    }

    public void setMatchGender(String matchGender) {
        this.matchGender = matchGender;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean addTagListSelected(String s) {
        Optional<String> result = tags.stream().filter(value -> value.equals(s)).findFirst();

        if (!result.isPresent() && tags.size() == 5) {
            return false;
        }

        if (result.isPresent()) {
            tags.remove(s);

            return false;
        }

        tags.add(s);
        return true;
    }

    public static class UserModelMapping {
        private String phone;
        private String password;
        private List<String> tags;
        private String uid, name, gender, birthday, matchGender, school, lastOperatingTime, typeLogin;
        private float matchDistance;
        private List<Float> matchAge;
        private Boolean active, working, statusMatchDistance, statusMatchAge;
        private LocationModel location;

        public UserModelMapping() {
        }


        public float getMatchDistance() {
            return matchDistance;
        }

        public void setMatchDistance(float matchDistance) {
            this.matchDistance = matchDistance;
        }

        public List<Float> getMatchAge() {
            return matchAge;
        }

        public void setMatchAge(List<Float> matchAge) {
            this.matchAge = matchAge;
        }

        public Boolean getStatusMatchDistance() {
            return statusMatchDistance;
        }

        public void setStatusMatchDistance(Boolean statusMatchDistance) {
            this.statusMatchDistance = statusMatchDistance;
        }

        public Boolean getStatusMatchAge() {
            return statusMatchAge;
        }

        public void setStatusMatchAge(Boolean statusMatchAge) {
            this.statusMatchAge = statusMatchAge;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTypeLogin() {
            return typeLogin;
        }

        public void setTypeLogin(String typeLogin) {
            this.typeLogin = typeLogin;
        }

        public LocationModel getLocation() {
            return location;
        }

        public void setLocation(LocationModel location) {
            this.location = location;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getMatchGender() {
            return matchGender;
        }

        public void setMatchGender(String matchGender) {
            this.matchGender = matchGender;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getLastOperatingTime() {
            return lastOperatingTime;
        }

        public void setLastOperatingTime(String lastOperatingTime) {
            this.lastOperatingTime = lastOperatingTime;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Boolean getWorking() {
            return working;
        }

        public void setWorking(Boolean working) {
            this.working = working;
        }
    }
}
