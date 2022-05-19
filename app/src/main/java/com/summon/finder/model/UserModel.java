package com.summon.finder.model;

import com.google.firebase.database.DataSnapshot;
import com.summon.finder.DAO.DAOUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UserModel {
    private DAOUser daoUser;
    private HashMap<String, String> images;
    private List<String> tags;
    private String uid, name, gender, birthday, matchGender, school, lastOperatingTime;
    private Boolean active, isWorking;

    public UserModel() {
        initData();
    }

    public UserModel(DataSnapshot dataSnapshot) {
        initData();
        UserModelMapping user = dataSnapshot.getValue(UserModelMapping.class);
        this.uid = user.uid;
        this.name = user.name;
        this.gender = user.gender;
        this.birthday = user.birthday;
        this.matchGender = user.matchGender;
        this.school = user.school;
        this.tags = user.tags;
        this.active = user.active;

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


    private static HashMap<String, String> convertArrayListToHashMap(ArrayList<String> arrayList) {
        HashMap<String, String> hashMap = new HashMap<>();

        int i = 1;
        for (String str : arrayList) {
            if (str == null) continue;

            hashMap.put(String.valueOf(i++), str);
        }

        return hashMap;
    }

    private void initData() {
        daoUser = new DAOUser();
        images = new HashMap<String, String>();
        tags = new ArrayList<>();
        uid = "";
        name = "";
        gender = "";
        birthday = "";
        matchGender = "";
        school = "";
        active = false;
    }

    public String getLastOperatingTime() {
        return lastOperatingTime;
    }

    public void setLastOperatingTime(String lastOperatingTime) {
        this.lastOperatingTime = lastOperatingTime;
    }

    public Boolean getWorking() {
        return isWorking;
    }

    public void setWorking(Boolean working) {
        isWorking = working;
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

    public void addImage(String key, String value) {
        images.put(key, value);
    }

    public void removeImage(String key) {
        images.remove(key);
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        daoUser.updateField("tags", tags);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        daoUser.updateField("uid", uid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        daoUser.updateField("name", name);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        daoUser.updateField("gender", gender);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        daoUser.updateField("birthday", birthday);
    }

    public String getMatchGender() {
        return matchGender;
    }

    public void setMatchGender(String matchGender) {
        this.matchGender = matchGender;
        daoUser.updateField("matchGender", matchGender);
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
        daoUser.updateField("school", school);
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        daoUser.update(this);
    }

    public Boolean addTagListSelected(String s) {
        Optional<String> result = tags.stream().filter(value -> value.equals(s)).findFirst();

        if (!result.isPresent() && tags.size() == 5) {
            return false;
        }

        if (result.isPresent()) {
            tags.remove(s);
            daoUser.updateField("tags", tags);
            return false;
        }

        tags.add(s);
        daoUser.updateField("tags", tags);
        return true;
    }

    public static class UserModelMapping {
        private List<String> tags = new ArrayList<>();
        private String uid = "";
        private String name = "";
        private String gender = "";
        private String birthday = "";
        private String matchGender = "";
        private String school = "";
        private String lastOperatingTime = "";
        private Boolean active = false;
        private Boolean isWorking = false;

        public UserModelMapping() {
        }

        public Boolean getWorking() {
            return isWorking;
        }

        public void setWorking(Boolean working) {
            isWorking = working;
        }

        public String getLastOperatingTime() {
            return lastOperatingTime;
        }

        public void setLastOperatingTime(String lastOperatingTime) {
            this.lastOperatingTime = lastOperatingTime;
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

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
